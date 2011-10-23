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
package org.overture.ide.core;



/**
 * Common protocol for Java elements that contain other Java elements.
 *
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IParent {
/**
 * Returns the immediate children of this element.
 * Unless otherwise specified by the implementing element,
 * the children are in no particular order.
 *
 * @exception JavaModelException if this element does not exist or if an
 *      exception occurs while accessing its corresponding resource
 * @return the immediate children of this element
 */
IVdmElement[] getChildren() throws VdmModelException;
/**
 * Returns whether this element has one or more immediate children.
 * This is a convenience method, and may be more efficient than
 * testing whether <code>getChildren</code> is an empty array.
 *
 * @exception JavaModelException if this element does not exist or if an
 *      exception occurs while accessing its corresponding resource
 * @return true if the immediate children of this element, false otherwise
 */
boolean hasChildren() throws VdmModelException;
}
