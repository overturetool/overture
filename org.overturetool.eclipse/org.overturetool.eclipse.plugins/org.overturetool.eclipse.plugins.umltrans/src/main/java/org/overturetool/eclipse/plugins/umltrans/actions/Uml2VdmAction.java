package org.overturetool.eclipse.plugins.umltrans.actions;

import java.io.FileNotFoundException;
import java.io.IOException;

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
import org.overturetool.umltrans.Main.Translator;

public class Uml2VdmAction implements IObjectActionDelegate
{

	private Shell shell;

	/**
	 * Constructor for Action1.
	 */
	public Uml2VdmAction()
	{
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

		org.eclipse.swt.widgets.FileDialog fd = new org.eclipse.swt.widgets.FileDialog(
				s, SWT.OPEN);
		fd.setText("Open");
		String[] filterExt = { "*.xml", "*.xmi" };
		fd.setFilterExtensions(filterExt);

		String inputFile = fd.open();
		if (inputFile != null)
		{
			try
			{

				
				IProject selectedProject = null;
				selectedProject = ProjectHelper.getSelectedProject(action, selectedProject);
				if(selectedProject==null)
				{
					ProjectHelper.ConsolePrint(shell,"Could not find selected project");
					return ;
				}

				translate(inputFile, selectedProject);

				

			} catch (Exception ex)
			{
				System.err.println(ex.getMessage() + ex.getStackTrace());
				
				ProjectHelper.ConsolePrint(shell, ex);
			}

		}

	}


	
	
	private void translate(final String inputFile,  final IProject selectedProject)
			throws FileNotFoundException, CGException, IOException
	{

		final Job expandJob = new Job("Translating VDM to UML")
		{

			@Override
			protected IStatus run(IProgressMonitor monitor)
			{

				monitor.worked(IProgressMonitor.UNKNOWN);
				try
				{
					Translator.TransLateUmlToVdm(
							inputFile,
							selectedProject.getLocation().toFile().getAbsolutePath());
				} catch (FileNotFoundException e)
				{
					
					e.printStackTrace();
					return new Status(IStatus.ERROR, "org.overturetool.umltrans",
							 "Translation error in file", e);
				} catch (CGException e)
				{
					
					e.printStackTrace();
					return new Status(IStatus.ERROR, "org.overturetool.umltrans",
							 "Translation error in specification", e);
				} catch (IOException e)
				{
					
					e.printStackTrace();
					return new Status(IStatus.ERROR, "org.overturetool.umltrans",
							 "Translation error in file", e);
				} catch (Exception e)
				{
				
					e.printStackTrace();
					return new Status(IStatus.ERROR, "org.overturetool.umltrans",
							 "Translation error", e);
				}

				shell.getDisplay().asyncExec(new Runnable()
				{

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

				return new Status(IStatus.OK, "org.overturetool.umltrans",
						IStatus.OK, "Translation completed", null);

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
