/*
 * #%~
 * Combinatorial Testing
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
package org.overture.ide.plugins.combinatorialtesting.internal;

import java.io.File;
import java.util.concurrent.TimeUnit;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.console.MessageConsoleStream;
import org.overture.combinatorialtesting.vdmj.server.ConnectionListener;
import org.overture.combinatorialtesting.vdmj.server.IClientMonitor;
import org.overture.ide.plugins.combinatorialtesting.ITracesConstants;
import org.overture.ide.plugins.combinatorialtesting.OvertureTracesPlugin;
import org.overture.ide.plugins.combinatorialtesting.store.StorageManager;

public class TraceTestEngine
{
	private Boolean isRunning = true;

	public void launch(final TraceExecutionSetup texe,
			final MessageConsoleStream out, final ITracesDisplay display)
	{

		Job job = new Job("Combinatorial Testing Runtime: " + texe.container)
		{

			@Override
			protected IStatus run(final IProgressMonitor monitor)
			{
				monitor.beginTask("Executing trace: " + texe.traceName, 100);
				IPreferenceStore preferences = OvertureTracesPlugin.getDefault().getPreferenceStore();

				if (!texe.coverageFolder.exists()
						&& !texe.coverageFolder.mkdirs())
				{
					System.out.println("Failed in creating coverage directory for CT:"
							+ texe.coverageFolder.getAbsolutePath());
				}
				File traceFolder = StorageManager.getCtOutputFolder(texe.project);// new File(outputFolder, "traces");
				traceFolder.mkdirs();
				Process p = null;

				ConnectionListener conn = null;
				try
				{
					Integer port = TestEngineDelegate.findFreePort();
					if (preferences.getBoolean(ITracesConstants.REMOTE_DEBUG_FIXED_PORT)
							&& preferences.getBoolean(ITracesConstants.REMOTE_DEBUG_PREFERENCE))
					{
						port = 1213;
					}

					final long startTime = System.currentTimeMillis();

					conn = new ConnectionListener(port, new IClientMonitor()
					{
						int worked = 0;

						public void initialize(String module)
						{
							try
							{
								out.println(texe.project.getName() + ":"
										+ module + " Initialized");
								monitor.subTask(module);
							} catch (Exception e)
							{
								e.printStackTrace();
							}
						}

						public void progress(String traceName, Integer progress)
						{
							long millis = System.currentTimeMillis()
									- startTime;

							String elapsed = String.format("%d min, %d sec.", TimeUnit.MILLISECONDS.toMinutes(millis), TimeUnit.MILLISECONDS.toSeconds(millis)
									- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));

							out.println(texe.project.getName() + ":"
									+ traceName + " Worked " + progress
									+ "%. Time elapsed: " + elapsed);

							int tmp = progress - worked;
							if (worked == 0)
							{
								worked = progress;
							} else
							{
								worked = progress;
							}

							if (tmp > 100)
							{
								tmp = 100;
							}
							monitor.worked(tmp);
						}

						public void completed()
						{
							long millis = System.currentTimeMillis()
									- startTime;

							String elapsed = String.format("%d min, %d sec.", TimeUnit.MILLISECONDS.toMinutes(millis), TimeUnit.MILLISECONDS.toSeconds(millis)
									- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));

							out.println(texe.project.getName()
									+ " Completed execution. Time elapsed: "
									+ elapsed);

							monitor.done();
							display.updateView(texe.project);
						}

						public void traceStart(String traceName)
						{
							out.println("Starting trace:" + traceName);
							monitor.subTask(traceName);
						}

						@Override
						public void traceError(String message)
						{

							out.println("Error CT runtime");
							out.println("Message: " + message);
							monitor.done();
							display.updateView(texe.project);
							threadFinished();
						}

						@Override
						public void terminating()
						{
							threadFinished();
						}
					});
					conn.start();
					p = new TestEngineDelegate().launch(texe, preferences, traceFolder, port, monitor);

				} catch (Exception e)
				{
					e.printStackTrace();
					return Status.CANCEL_STATUS;
				}

				while (!monitor.isCanceled() && isRunning)
				{
					millisleep();
				}

				if (isRunning)
				{
					try
					{
						if (p != null)
						{
							p.exitValue();
						}
					} catch (Exception e)
					{
						if (p != null)
						{
							p.destroy();
						}
					}
				}

				if (conn != null)
				{
					conn.die();
				}

				try
				{
					((IProject) texe.project.getAdapter(IProject.class)).refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
				} catch (CoreException e)
				{

				}

				monitor.done();
				return Status.OK_STATUS;
			}

			private void millisleep()
			{
				try
				{
					Thread.sleep(500);
				} catch (InterruptedException e)
				{
				}
			}

		};

		job.schedule();
	}

	private void threadFinished()
	{
		this.isRunning = false;
	}
}
