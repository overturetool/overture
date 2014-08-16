/*
 * #%~
 * org.overture.ide.core
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
package org.overture.ide.core.resources;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdapterFactory;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.node.INode;
import org.overture.ide.core.IVdmModel;
import org.overture.ide.internal.core.ast.VdmModelManager;

public class AdapterFactoryNode implements IAdapterFactory
{

	@Override
	public Object getAdapter(Object adaptableObject,
			@SuppressWarnings("rawtypes") Class adapterType)
	{
		if (adapterType == IFile.class)
		{
			if (adaptableObject instanceof INode)
			{
				INode node = (INode) adaptableObject;

				//Search dept is here to protect against loops in the tree
				int searchDept = 5;
				while (--searchDept >0 && !(node instanceof SClassDefinition || node instanceof AModuleModules)
						&& (node = node.parent()) != null)
					;

				if (node instanceof SClassDefinition|| node instanceof AModuleModules)
				{
					return getIFileFromRootINode(node);
				}
			}
		}
		return null;
	}

	protected IFile getIFileFromRootINode(INode node)
	{
		List<IProject> projects = VdmModelManager.getInstance().getProjects();
		for (IProject iProject : projects)
		{
			IVdmProject p = (IVdmProject) iProject.getAdapter(IVdmProject.class);
			// we know this is ok, but we check anyway
			if (p != null)
			{
				IVdmModel model = p.getModel();
				if (model.getRootElementList().contains(node))
				{
					// ok we have the right project
					for (IVdmSourceUnit su : model.getSourceUnits())
					{
						if (su.getParseList().contains(node))
						{
							return su.getFile();
						}
					}
				}
			}
		}
		return null;
	}

	@SuppressWarnings("rawtypes")
	public Class[] getAdapterList()
	{
		return new Class[] { INode.class };
	}

}
