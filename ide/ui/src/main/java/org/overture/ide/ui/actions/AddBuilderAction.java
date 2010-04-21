package org.overture.ide.ui.actions;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.internal.ObjectPluginAction;
import org.overture.ide.core.ICoreConstants;
import org.overture.ide.core.resources.VdmProject;


@SuppressWarnings("restriction")
public class AddBuilderAction implements IObjectActionDelegate
{
	

	public void setActivePart(IAction action, IWorkbenchPart targetPart)
	{
		

	}

	public void run(IAction action)
	{
		
		try
		{
			IProject selectedProject = null;
			selectedProject = getSelectedProject(action, selectedProject);
			if (selectedProject == null)
			{
				System.err.println("Could not find selected project");
				return;
			}
			VdmProject.addBuilder(selectedProject, ICoreConstants.BUILDER_ID,"","");

		} catch (Exception ex)
		{
			System.err.println(ex.getMessage() + ex.getStackTrace());

		}

	}

	public void selectionChanged(IAction action, ISelection selection)
	{
		

	}

	public static IProject getSelectedProject(IAction action,
			IProject selectedProject)
	{
		if (action instanceof ObjectPluginAction)
		{
			ObjectPluginAction objectPluginAction = (ObjectPluginAction) action;
			if (objectPluginAction.getSelection() instanceof ITreeSelection)
			{
				ITreeSelection selection = (ITreeSelection) objectPluginAction.getSelection();
				if (selection.getPaths().length > 0)
					selectedProject = (IProject) selection.getPaths()[0].getFirstSegment();
			} else if (objectPluginAction.getSelection() instanceof IStructuredSelection)
			{
				IStructuredSelection selection = (IStructuredSelection) objectPluginAction.getSelection();
				if (selection.getFirstElement() instanceof IProject)
					selectedProject = (IProject) selection.getFirstElement();
			}
		}
		return selectedProject;
	}

}
