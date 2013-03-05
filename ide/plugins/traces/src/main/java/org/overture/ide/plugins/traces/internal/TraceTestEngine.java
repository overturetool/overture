package org.overture.ide.plugins.traces.internal;

import java.io.File;

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
import org.overture.ide.plugins.traces.ITracesConstants;
import org.overture.ide.plugins.traces.OvertureTracesPlugin;
import org.overture.ide.plugins.traces.store.StorageManager;
import org.overturetool.traces.vdmj.server.ConnectionListener;
import org.overturetool.traces.vdmj.server.IClientMonitor;

public class TraceTestEngine
{
	private Boolean isRunning = true;

	public void launch(final TraceExecutionSetup texe,
			final MessageConsoleStream out, final ITracesDisplay display)
	{

		Job job = new Job("Combinatorial Testing RuntimeT: " + texe.container)
		{

			@Override
			protected IStatus run(final IProgressMonitor monitor)
			{
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
					if (preferences.getBoolean(ITracesConstants.REMOTE_DEBUG_FIXED_PORT) && preferences.getBoolean(ITracesConstants.REMOTE_DEBUG_PREFERENCE))
					{
						port = 1213;
					}
					conn = new ConnectionListener(port, new IClientMonitor()
					{

						public void initialize(String module)
						{
							// System.out.println("CT init recieved");
							out.println("Initialized: " + module);
							monitor.subTask(module);
						}

						public void progress(String traceName, Integer progress)
						{
							// System.out.println("CT progress " + traceName + " "
							// + progress);
							out.println("Worked(" + traceName + "): "
									+ progress);
							// monitor.worked(progress);
						}

						public void completed()
						{
							out.println("Completed CT runtime");

							monitor.done();
							display.updateView(texe.project);
							threadFinished();
						}

						public void traceStart(String traceName)
						{
							out.println("Starting trace:" + traceName);
							monitor.subTask(traceName);
						}

						@Override
						public void traceError(String message) {
							
							out.println("Error CT runtime");
							out.println("Message: " + message);
							monitor.done();
							display.updateView(texe.project);
							threadFinished();
						}
					});
					conn.start();
					// System.out.println("Starting CT runtime with: "
					// + texe.container + "-" + texe.traceName);
					p = new TestEngineDelegate().launch(texe, preferences, traceFolder, port);

				} catch (Exception e)
				{
					e.printStackTrace();
					return Status.CANCEL_STATUS;
				}

				while (!monitor.isCanceled() && isRunning)
				{
					millisleep();
				}
				// System.out.println("CT eval runtime finished");

				if (isRunning)
				{
					try
					{
						if(p!=null)
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
