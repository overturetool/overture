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

/**
 * Stores informaton of a vdm class.
 * @author carlos
 *
 */
public class VdmClass implements IVdmDefinition {

	private Vector<VdmAnnotation> annotations = null;
	private Vector<IVdmDefinition> definitions = null;
	private String name = null;
	private boolean hasConstructors = false;
	
	/**
	 * 
	 * @param name Name of the class
	 * @param hasConstructors If the class has one or more constructors this should be set to true.
	 */
	public VdmClass(String name, boolean hasConstructors) {
		this.name = name; this.hasConstructors = hasConstructors;
	}
	
	@Override
	public String getName() {
		return name;
	}
 
	@Override
	public Vector<IVdmDefinition> getDefinitions() {
		return definitions;
	}

	@Override
	public void addDefinition(IVdmDefinition def) {
		if ( definitions == null )
			definitions = new Vector<IVdmDefinition>();
		definitions.add(def);
	}

	@Override
	public boolean hasAnnotations() {
		if(annotations == null)
			return false;
		return true;
	}

	@Override
	public Vector<VdmAnnotation> getAnnotations() {
		return annotations;
	}

	@Override
	public boolean hasDefinitions() {
		if(definitions == null)
			return false;
		return true;
	}
	
	public boolean hasConstructors() {
		return hasConstructors;
	}

	@Override
	public void addAnnotation(VdmAnnotation an) {
		if ( annotations == null )
			annotations = new Vector<VdmAnnotation>();
		annotations.add(an);
	}


}
