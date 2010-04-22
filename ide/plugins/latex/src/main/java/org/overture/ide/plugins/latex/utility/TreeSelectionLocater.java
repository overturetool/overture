package org.overture.ide.plugins.latex.utility;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.ui.internal.ObjectPluginAction;
import org.overture.ide.core.resources.IVdmProject;
import org.overture.ide.core.resources.VdmProject;

@SuppressWarnings("restriction")
public class TreeSelectionLocater
{
	public static IVdmProject getSelectedProject(IAction action)
	{
		IVdmProject project = null;
		if (action instanceof ObjectPluginAction)
		{
			ObjectPluginAction objectPluginAction = (ObjectPluginAction) action;
			if (objectPluginAction.getSelection() instanceof ITreeSelection)
			{
				ITreeSelection selection = (ITreeSelection) objectPluginAction.getSelection();
				if (selection.getPaths().length > 0)
				{
					IProject p = (IProject) selection.getPaths()[0].getFirstSegment();
					if (VdmProject.isVdmProject(p))
					{
						project = VdmProject.createProject(p);
					}
				}
			} else if (objectPluginAction.getSelection() instanceof IStructuredSelection)
			{
				IStructuredSelection selection = (IStructuredSelection) objectPluginAction.getSelection();
				if (selection.getFirstElement() instanceof IProject)
				{
					IProject p = (IProject) selection.getFirstElement();
					if (VdmProject.isVdmProject(p))
					{
						project = VdmProject.createProject(p);
					}
				}
			}
		}
		return project;
	}
}
