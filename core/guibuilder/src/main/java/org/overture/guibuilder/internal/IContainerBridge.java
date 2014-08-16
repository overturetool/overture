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
package org.overture.guibuilder.internal;

import java.awt.event.ActionListener;
import java.io.File;

/**
 * Interface for all container bridges. A container bridge as well as initializing a user interface window endows it
 * with functionality, serving as a backend.
 * 
 * @author carlos
 */
public interface IContainerBridge extends ActionListener
{

	/**
	 * Initializes the ui window according to a description stored in file.
	 * 
	 * @param file
	 *            File containing the ui description.
	 * @throws Exception
	 */
	void buildComponent(File file) throws Exception;

	/**
	 * Sets the visibility of the window.
	 * 
	 * @param b
	 */
	void setVisible(boolean b);

	/**
	 * The id of the container bridge as well as the ui window.
	 * 
	 * @return The id in string form.
	 */
	String getId();
}
