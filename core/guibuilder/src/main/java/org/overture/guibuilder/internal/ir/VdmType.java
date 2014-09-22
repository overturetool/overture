/*
 * #%~
 * Overture GUI Builder
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
/*******************************************************************************
 * Copyright (c) 2009, 2013 Overture Team and others.
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
package org.overture.guibuilder.internal.ir;

/**
 * Class to represent a vdm++ type
 * 
 * @author carlos
 */
public class VdmType
{

	String name = null;
	Boolean isClass = false;

	/**
	 * Constructor
	 * 
	 * @param name
	 *            - Name of the type (the type is identified by its name)
	 * @param isClass
	 *            - Is this a type of class.
	 */
	public VdmType(String name, boolean isClass)
	{
		this.name = name;
		this.isClass = isClass;
	}

	/**
	 * isClass attribute.
	 * 
	 * @return - true if this is a type of class
	 */
	public boolean isClass()
	{
		return isClass;
	}

	/**
	 * Returns the name of the type. The VDM++ types representation is identified by its name.
	 * 
	 * @return - returns the name of the type.
	 */
	public String getName()
	{
		return name;
	}

}
