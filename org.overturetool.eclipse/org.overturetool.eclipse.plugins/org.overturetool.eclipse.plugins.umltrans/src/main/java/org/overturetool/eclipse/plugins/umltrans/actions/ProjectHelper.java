package org.overturetool.eclipse.plugins.umltrans.actions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.ui.internal.ObjectPluginAction;

public class ProjectHelper
{
	public static List<IFile> getAllMemberFiles(IContainer dir, String[] exts)
	{
		ArrayList<IFile> list = new ArrayList<IFile>();
		IResource[] arr = null;
		try
		{
			arr = dir.members();
		} catch (CoreException e)
		{
		}

		for (int i = 0; arr != null && i < arr.length; i++)
		{
			if (arr[i].getType() == IResource.FOLDER)
			{
				list.addAll(getAllMemberFiles((IFolder) arr[i], exts));
			} else
			{
				for (int j = 0; j < exts.length; j++)
				{
					if (exts[j].equalsIgnoreCase(arr[i].getFileExtension()))
					{
						list.add((IFile) arr[i]);
						break;
					}
				}
			}
		}
		return list;
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
