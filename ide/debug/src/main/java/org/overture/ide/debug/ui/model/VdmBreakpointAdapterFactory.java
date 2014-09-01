/*
 * #%~
 * org.overture.ide.debug
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
package org.overture.ide.debug.ui.model;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.texteditor.ITextEditor;
import org.overture.ide.core.resources.IVdmProject;

public class VdmBreakpointAdapterFactory implements IAdapterFactory
{

	public Object getAdapter(Object adaptableObject,
			@SuppressWarnings("rawtypes") Class adapterType)
	{
		if (adaptableObject instanceof ITextEditor)
		{
			ITextEditor editorPart = (ITextEditor) adaptableObject;
			IResource resource = (IResource) editorPart.getEditorInput().getAdapter(IResource.class);
			if (resource != null && resource instanceof IFile)
			{
				IFile file = (IFile) resource;
				try
				{
					if (file == null || !file.exists()
							|| !file.isSynchronized(IResource.DEPTH_ZERO))
					{
						return null;
					}
					IVdmProject project = (IVdmProject) file.getProject().getAdapter(IVdmProject.class);
					if (project != null && project.isModelFile(file))
					{
						return new VdmLineBreakpointAdapter();
					}
				} catch (CoreException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
		return null;
	}

	@SuppressWarnings("rawtypes")
	public Class[] getAdapterList()
	{
		// TODO Auto-generated method stub
		return null;
	}

}
