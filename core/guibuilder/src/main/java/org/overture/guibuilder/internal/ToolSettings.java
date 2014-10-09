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

/**
 * This class stores the settings of the tool.
 */
public class ToolSettings
{

	/**
	 * Enumeration of generation modes.
	 * 
	 * @author carlos
	 */
	public enum GENERATION_MODE
	{
		ANNOTATIONS, NO_ANNOTATIONS
	};

	/**
	 * Generation mode of the tool. By default the tool uses no annotation in the generation process.
	 */
	public static GENERATION_MODE GENERATION_SETTINGS = GENERATION_MODE.NO_ANNOTATIONS;
	/**
	 * Flag for saving xml. If true the xml description of the generated user interface. is saved.
	 */
	public static Boolean SAVE_XML = false;
	/**
	 * Flag for generation. If true a new user interface is generated. If false the tool will use a previously generated
	 * user interface.
	 */
	public static Boolean GENERATE = true;

}
