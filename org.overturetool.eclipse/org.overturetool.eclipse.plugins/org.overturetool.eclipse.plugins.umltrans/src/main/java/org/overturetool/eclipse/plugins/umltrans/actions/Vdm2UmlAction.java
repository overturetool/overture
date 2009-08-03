package org.overturetool.eclipse.plugins.umltrans.actions;

import java.io.File;
import java.util.List;
import java.util.Vector;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.internal.ObjectPluginAction;
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
		if (action instanceof ObjectPluginAction)
		{
			ObjectPluginAction objectPluginAction = (ObjectPluginAction) action;
			if (objectPluginAction.getSelection() instanceof ITreeSelection)
			{
				ITreeSelection selection = (ITreeSelection) objectPluginAction.getSelection();
				if (selection.getPaths().length > 0)
					selectedProject = (IProject) selection.getPaths()[0].getFirstSegment();
			}
		}

		String[] filterExt = { "vpp", "tex" };

		try
		{

			List<IFile> files = ProjectHelper.getAllMemberFiles(
					selectedProject,
					filterExt);
			List<String> filesPathes = new Vector<String>();
			for (IFile file : files)
			{
				filesPathes.add(file.getLocation().toFile().getAbsolutePath());
			}

			String outFile = new File(selectedProject.getLocation().toFile(),
					selectedProject.getName() + ".xmi").getAbsolutePath();
			if (outFile != null)
			{

				if (!(outFile.endsWith(".xml") || outFile.endsWith(".xml")))
					outFile += ".xml";
				Translator.TransLateTexVdmToUml(filesPathes, outFile);

				MessageDialog.openInformation(
						shell,
						"Vdm 2 Uml",
						"Processing completed: " + outFile);

			}

		} catch (Exception ex)
		{
			System.err.println(ex.getMessage() + ex.getStackTrace());
			MessageDialog.openInformation(
					shell,
					"Error",
					"Processing completed with errors");
		}

	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection)
	{
	}

}
