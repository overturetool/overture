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
 * A individual annotation of a vdm class or a vdm operation/function.
 * 
 * @author carlos
 *
 */
public class VdmAnnotation {

	private String name = null;
	private String value = null;

	/**
	 * 
	 * @param name Name of the annotation (ex.: the name of "check=id" is "check")
	 * @param value Value of the annotation (ex.: the value of "check=id" is "id")
	 */
	public VdmAnnotation(String name, String value) {
		this.name = name; this.value = value;
	}

	/**
	 * Returns the name of the annotation (ex.: the name of "check=id" is "check")
	 * @return Name of the annotation
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the value of the annotation (ex.: the value of "check=id" is "id")
	 * @return The value of the annotation
	 */
	public String getValue() {
		return value;
	}
	
}
