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

import java.io.File;
import java.util.Vector;

import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

/**
 * Interface for a vdm class reader.
 * 
 * @author carlos
 *
 */
public interface IVdmClassReader {

	/**
	 * Reads the vdm specification, and builds an internal
	 * representation
	 * @param files List of files containing the vdm specification
	 */
	abstract void readFiles(Vector<File> files, ITypeCheckerAssistantFactory af);
	/**
	 * Returns a list of the vdm classes that compose the vdm specification
	 * @return List of vdm classes
	 */
	abstract Vector<IVdmDefinition> getClassList();
	/**
	 * Returns the list of the vdm class (names) that compose the specification 
	 * @return List of class names
	 */
	abstract Vector<String> getClassNames();
	
}
