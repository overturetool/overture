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

@SuppressWarnings("restriction")
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
