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
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.overture.ide.core.resources.IVdmProject;

public class VdmResourcePropertyTester extends PropertyTester
{

	private static final String PROPERTY = "dialect";

	public boolean test(Object receiver, String property, Object[] args,
			Object expectedValue)
	{
		if (receiver instanceof IVdmProject)
		{
			if (property.equalsIgnoreCase(PROPERTY))
			{
				return ((IVdmProject) receiver).getDialect().name()
						.equalsIgnoreCase(expectedValue.toString());
			}
		} else if (receiver instanceof IFile)
		{
			IFile file = (IFile) receiver;

			IVdmProject vdmProject = (IVdmProject) file.getProject()
					.getAdapter(IVdmProject.class);

			if (vdmProject != null)
			{

				if (property.equalsIgnoreCase(PROPERTY))
				{
					return vdmProject.getDialect().name().equalsIgnoreCase(
							expectedValue.toString());
				}
			}
		} else if (receiver instanceof IFolder)
		{
			IFolder file = (IFolder) receiver;
			IVdmProject vdmProject = (IVdmProject) file.getProject()
					.getAdapter(IVdmProject.class);

			if (vdmProject != null)
			{

				if (property.equalsIgnoreCase(PROPERTY))
				{
					return vdmProject.getDialect().name().equalsIgnoreCase(
							expectedValue.toString());
				}
			}
		}

		return false;
	}

}
