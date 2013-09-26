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
package org.overture.ide.ui.navigator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.ui.internal.util.Util;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.navigator.IDescriptionProvider;
import org.overture.ast.node.INode;
import org.overture.ide.core.resources.IVdmProject;
import org.overture.ide.core.resources.IVdmSourceUnit;
import org.overture.ide.ui.VdmPluginImages;

@SuppressWarnings("restriction")
public class VdmNavigatorLabelProvider extends WorkbenchLabelProvider implements
		ILabelProvider, IDescriptionProvider
{
	
	public String getDescription(Object anElement)
	{
		if (anElement instanceof IResource)
		{
			return ((IResource) anElement).getFullPath().makeRelative().toString();
		}
		return null;
	}
	
	protected IWorkbenchAdapter getAdapterVdm(Object element)
	{
		if (element instanceof IFile)
		{
			IVdmSourceUnit source = (IVdmSourceUnit) Util.getAdapter(((IFile) element), IVdmSourceUnit.class);
			IWorkbenchAdapter adapter = getAdapter(source);
			return adapter;
		}
		if (element instanceof INode)
		{
			System.out.println(element);
		}
		return super.getAdapter(element);
	}

	@Override
	protected ImageDescriptor decorateImage(ImageDescriptor input,
			Object element)
	{
		// Adds package icon to folder if the folder is in the build path
		if (element instanceof IFolder)
		{
			IFolder folder = (IFolder) element;
			IVdmProject project = (IVdmProject) folder.getProject().getAdapter(IVdmProject.class);
			if (project != null)
			{
				if (project.getModelBuildPath().contains(folder))
				{
					return VdmPluginImages.getDescriptor(VdmPluginImages.IMG_OBJS_PACKFRAG_ROOT);
				} else if (project.getModelBuildPath().getOutput() != null
						&& project.getModelBuildPath().getOutput().equals(folder))
				{
					return VdmPluginImages.getDescriptor(VdmPluginImages.IMG_OBJS_CLASSFOLDER);
				}
			}
		}
		return super.decorateImage(input, element);
	}
}
