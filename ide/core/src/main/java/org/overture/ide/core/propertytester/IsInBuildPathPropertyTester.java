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
package org.overture.ide.core.propertytester;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.overture.ide.core.resources.IVdmProject;
import org.overture.ide.core.resources.ModelBuildPath;
/**
 * Property: IsInBuildPath
 * If expectedValue is true it will return TRUE if the receiver is in the path. If the
 * expectedValue is false it will return TRUE if the receiver is not in the path.
 * @author kela
 *
 */
public class IsInBuildPathPropertyTester extends PropertyTester
{
	final static String PROPERTY = "IsInBuildPath";

	public boolean test(Object receiver, String property, Object[] args,
			Object expectedValue)
	{
		IVdmProject project = null;
		boolean checkIfInSourcePath = true;
		if (expectedValue instanceof Boolean)
		{
			checkIfInSourcePath = (Boolean) expectedValue;
		}
		if (receiver instanceof IProject)
		{
			project = (IVdmProject) ((IProject) receiver).getAdapter(IVdmProject.class);

		} else if (receiver instanceof IVdmProject)
		{
			project = (IVdmProject) receiver;
		}

		if (project != null && property.equalsIgnoreCase(PROPERTY))
		{
			ModelBuildPath path = project.getModelBuildPath();

			if (checkIfInSourcePath)
			{
				return path.contains((IProject) project.getAdapter(IProject.class));
			} else
			{
				return !path.contains((IProject) project.getAdapter(IProject.class));
			}

		} else if (receiver instanceof IFolder)
		{
			IFolder file = (IFolder) receiver;

			project = (IVdmProject) file.getProject().getAdapter(IVdmProject.class);

			if (project != null)
			{
				ModelBuildPath path = project.getModelBuildPath();
				if (property.equalsIgnoreCase(PROPERTY))
				{
					if (checkIfInSourcePath)
					{
						return path.getModelSrcPaths().contains(file);
					} else
					{
						return !path.getModelSrcPaths().contains(file);
					}
				}
			}
		}
		return false;
	}

}
