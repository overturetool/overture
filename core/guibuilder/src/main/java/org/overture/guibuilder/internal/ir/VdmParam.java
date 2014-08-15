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
 * Class to represent a vdm++ operation/function parameter
 * 
 * @author carlos
 */
public class VdmParam
{

	private String paramName = null;
	private VdmType paramType = null;

	/**
	 * @param paramName
	 *            The name given to the parameter
	 * @param paramType
	 *            The type of the parameter
	 */
	public VdmParam(String paramName, VdmType paramType)
	{
		this.paramName = paramName;
		this.paramType = paramType;
	}

	/**
	 * @return the name of the parameter
	 */
	public String getParaName()
	{
		return paramName;
	}

	/**
	 * @return the paramters type
	 */
	public VdmType getType()
	{
		return paramType;
	}
}
