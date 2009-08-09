package org.overturetool.eclipse.plugins.umltrans.actions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import jp.co.csk.vdm.toolbox.VDM.CGException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.overturetool.umltrans.Main.Translator;

public class Vdm2UmlAction implements IObjectActionDelegate
{

	private Shell shell;

	/**
	 * Constructor for Action1.
	 */
	public Vdm2UmlAction()
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
		IProject selectedProject = null;
		selectedProject = ProjectHelper.getSelectedProject(
				action,
				selectedProject);
		if (selectedProject == null)
		{
			ConsoleWriter.ConsolePrint(shell, "Could not find selected project");
			return;
		}

		String[] filterExt = { "vpp", "tex" };

		try
		{

			List<IFile> files = ProjectHelper.getAllMemberFiles(
					selectedProject,
					filterExt);
			List<File> filesPathes = new Vector<File>();
			for (IFile file : files)
			{
				filesPathes.add(file.getLocation().toFile());
			}

			File outFile = new File(selectedProject.getLocation().toFile(),
					selectedProject.getName() + ".xmi");
			if (outFile != null)
			{

//				if (!(outFile.endsWith(".xml") || outFile.endsWith(".xml")))
//					outFile += ".xml";
				translate(filesPathes, outFile,selectedProject);


			}

		} catch (Exception ex)
		{
			System.err.println(ex.getMessage() + ex.getStackTrace());
			ConsoleWriter.ConsolePrint(shell, ex);

		}

	}

	private void translate(final List<File> filesPathes,
			final File outFile, final IProject selectedProject)
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
					Translator.setOutput(new ConsoleWriter(shell));
					Translator.TransLateTexVdmToUml(filesPathes, outFile);
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
