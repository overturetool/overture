/*******************************************************************************
 * Copyright (c) 2009, 2011 Overture Team and others.
 *
 * Overture is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Overture is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Overture.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * The Overture Tool web-site: http://overturetool.org/
 *******************************************************************************/
package org.overture.ide.plugins.umltrans.actions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import jp.co.csk.vdm.toolbox.VDM.CGException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.overturetool.umltrans.Main.CmdLineProcesser;

public class Uml2VdmAction implements IObjectActionDelegate
{

	private Shell shell;

	/**
	 * Constructor for Action1.
	 */
	public Uml2VdmAction() {
		super();
	}

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart)
	{
		shell = targetPart.getSite().getShell();
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action)
	{
		org.eclipse.swt.widgets.Shell s = new org.eclipse.swt.widgets.Shell();

		org.eclipse.swt.widgets.FileDialog fd = new org.eclipse.swt.widgets.FileDialog(s,
				SWT.OPEN);
		fd.setText("Open");
		String[] filterExt = { "*.xml", "*.xmi" };
		fd.setFilterExtensions(filterExt);

		File inputFile = new File(fd.open());
		if (inputFile != null)
		{
			try
			{

				IProject selectedProject = null;
				selectedProject = ProjectHelper.getSelectedProject(action,
						selectedProject);
				if (selectedProject == null)
				{
					ConsoleWriter.ConsolePrint(shell,
							"Could not find selected project");
					return;
				}

				translate(inputFile, selectedProject);

			} catch (Exception ex)
			{
				System.err.println(ex.getMessage() + ex.getStackTrace());

				ConsoleWriter.ConsolePrint(shell, ex);
			}

		}

	}

	private void translate(final File inputFile, final IProject selectedProject)
			throws FileNotFoundException, CGException, IOException
	{

		final Job expandJob = new Job("Translating VDM to UML") {

			@Override
			protected IStatus run(IProgressMonitor monitor)
			{

				monitor.worked(IProgressMonitor.UNKNOWN);
				try
				{
					List<File> inputFiles = new Vector<File>();
					inputFiles.add(inputFile);
					CmdLineProcesser.toVpp(selectedProject.getLocation().toFile(), inputFiles);
				} catch (FileNotFoundException e)
				{
					ConsoleWriter.ConsolePrint(shell, e);
					e.printStackTrace();
					return new Status(IStatus.ERROR,
							"org.overture.ide.umltrans",
							"Translation error in file",
							e);
				} catch (CGException e)
				{
					ConsoleWriter.ConsolePrint(shell, e);
					e.printStackTrace();
					return new Status(IStatus.ERROR,
							"org.overture.ide.umltrans",
							"Translation error in specification",
							e);
				} catch (IOException e)
				{
					ConsoleWriter.ConsolePrint(shell, e);
					e.printStackTrace();
					return new Status(IStatus.ERROR,
							"org.overture.ide.umltrans",
							"Translation error in file",
							e);
				} catch (Exception e)
				{

					e.printStackTrace();
					ConsoleWriter.ConsolePrint(shell, e);
					return new Status(IStatus.ERROR,
							"org.overture.ide.umltrans",
							"Translation error",
							e);
				}

				shell.getDisplay().asyncExec(new Runnable() {

					public void run()
					{
						try
						{
							selectedProject.refreshLocal(1, null);
						} catch (CoreException e)
						{

							e.printStackTrace();
						}
					}

				});

				monitor.done();
				// expandCompleted = true;

				return new Status(IStatus.OK,
						"org.overture.ide.umltrans",
						IStatus.OK,
						"Translation completed",
						null);

			}

		};
		expandJob.setPriority(Job.INTERACTIVE);
		expandJob.schedule(0);

	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection)
	{
	}

}
