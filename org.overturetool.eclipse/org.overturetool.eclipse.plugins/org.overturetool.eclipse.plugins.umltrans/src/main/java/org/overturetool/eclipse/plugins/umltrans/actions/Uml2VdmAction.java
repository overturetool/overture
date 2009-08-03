package org.overturetool.eclipse.plugins.umltrans.actions;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.internal.ObjectPluginAction;
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

				Translator.TransLateUmlToVdm(
						inputFile,
						selectedProject.getLocation().toFile().getAbsolutePath());

				MessageDialog.openInformation(
						shell,
						"Uml 2 Vdm",
						"Processing completed");

			} catch (Exception ex)
			{
				System.err.println(ex.getMessage() + ex.getStackTrace());
				MessageDialog.openInformation(
						shell,
						"Error",
						"Processing completed with errors");
			}

		}

	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection)
	{
	}

}
