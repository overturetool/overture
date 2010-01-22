package org.overture.ide.plugins.latex.utility;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.ui.internal.ObjectPluginAction;

@SuppressWarnings("restriction")
public class TreeSelectionLocater {
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
