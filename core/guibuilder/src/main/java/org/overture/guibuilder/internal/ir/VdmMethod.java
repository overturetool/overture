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

import java.util.Vector;

// vdm++ has operations and functions, but for simplicity sake, we'll
// call them methods.
/**
 * Class representing a vdm++ "method".
 */
public class VdmMethod implements IVdmDefinition
{

	private Vector<VdmAnnotation> annotations = null;
	private Vector<IVdmDefinition> definitions = null;
	private Vector<VdmParam> params = null;
	private String name = null;
	private boolean isConstructor = false;
	private VdmType methodType = null;

	/**
	 * Class constructor.
	 * 
	 * @param name
	 *            The name of the method.
	 * @param constructorFlag
	 *            True if method is a constructor
	 * @param methodType
	 *            the type of the method
	 */
	public VdmMethod(String name, boolean constructorFlag, VdmType methodType)
	{
		this.name = name;
		isConstructor = constructorFlag;
		params = new Vector<VdmParam>();
		this.methodType = methodType;
	}

	@Override
	public String getName()
	{
		return name;
	}

	/**
	 * The type of the method.
	 * 
	 * @return The VdmType of the method.
	 */
	public VdmType getType()
	{
		return methodType;
	}

	@Override
	public Vector<IVdmDefinition> getDefinitions()
	{
		return definitions;
	}

	@Override
	public void addDefinition(IVdmDefinition def)
	{
		if (definitions == null)
		{
			definitions = new Vector<IVdmDefinition>();
		}
		definitions.add(def);
	}

	@Override
	public boolean hasAnnotations()
	{
		if (annotations == null)
		{
			return false;
		}
		return true;
	}

	@Override
	public Vector<VdmAnnotation> getAnnotations()
	{
		return annotations;
	}

	@Override
	public boolean hasDefinitions()
	{
		if (definitions == null)
		{
			return false;
		}
		return true;
	}

	/**
	 * Checks if the method is a constructor
	 * 
	 * @return Is the method a constructor
	 */
	public boolean isConstructor()
	{
		return isConstructor;
	}

	@Override
	public void addAnnotation(VdmAnnotation an)
	{
		if (annotations == null)
		{
			annotations = new Vector<VdmAnnotation>();
		}
		annotations.add(an);
	}

	/**
	 * Adds a parameter to the method
	 * 
	 * @param param
	 *            the paramater to add to the method
	 */
	public void addParam(VdmParam param)
	{
		params.add(param);
	}

	/**
	 * Returns the list of parameters of the method
	 * 
	 * @return The list of parameters
	 */
	public Vector<VdmParam> getParamList()
	{
		return params;
	}

	/*
	 * public boolean isVoid() { if (this.type == null) return true; return false; } public void setType(String s) {
	 * this.type = s; }
	 */
}
