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
 * A vdm definition.
 * 
 * @author carlos
 *
 */
public interface IVdmDefinition {

	/**
	 * Returns the name of the definition
	 * @return The name.
	 */
	public String getName();
	/**
	 * Returns the list of definitions contained (children)
	 * @return The definition list.
	 */
	public Vector<IVdmDefinition> getDefinitions();
	/**
	 * Adds a definition (adds child)
	 * @param def
	 */
	public void addDefinition( IVdmDefinition def);
	/**
	 * Adds a new annotation to the definition
	 * @param an the VdmAnnotation
	 */
	public void addAnnotation(VdmAnnotation an);
	/**
	 * Checks if has definition (has children)
	 * @return True if the definition has sub definitions.
	 */	
	public boolean hasDefinitions();
	/**
	 * Checks if has annotations
	 * @return True if the definition is annotated.
	 */
	public boolean hasAnnotations();
	/**
	 * Returns the list of annotations if any (null if there isn't)
	 * @return The list of annotations.
	 */
	public Vector<VdmAnnotation> getAnnotations();
	
}
