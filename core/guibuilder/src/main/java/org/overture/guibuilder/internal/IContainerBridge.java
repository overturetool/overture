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
package org.overture.guibuilder.internal;

import java.awt.event.ActionListener;
import java.io.File;

/**
 * Interface for all container bridges. A container bridge as well as initializing a user interface
 * window endows it with functionality, serving as a backend.
 * @author carlos
 *
 */
public interface IContainerBridge extends ActionListener {

	/**
	 * Initializes the ui window according to a description stored in file.
	 * @param file File containing the ui description.
	 * @throws Exception
	 */
	void buildComponent(File file) throws Exception;

	/**
	 * Sets the visibility of the window.
	 * @param b
	 */
	void setVisible(boolean b);
	
	/**
	 * The id of the container bridge as well as the ui window.
	 * @return The id in string form.
	 */
	String getId();
}
