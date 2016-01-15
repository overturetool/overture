/*
 * #%~
 * Code Generator Plugin
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
package org.overture.ide.plugins.codegen;

public interface ICodeGenConstants
{
	public static final String PLUGIN_ID = "org.overture.ide.plugins.codegen";
	public static final String CONSOLE_NAME = "Code Generator Console";

	public static final String GENERATE_CHAR_SEQUENCES_AS_STRINGS = PLUGIN_ID
			+ ".char_sequences_as_strings";
	public static final boolean GENERATE_CHAR_SEQUENCES_AS_STRING_DEFAULT = true;

	public static final String DISABLE_CLONING = PLUGIN_ID + ".disable_cloning";
	public static final boolean DISABLE_CLONING_DEFAULT = false;
	
	public static final String GENERATE_CONCURRENCY_MECHANISMS = PLUGIN_ID + ".concurrency_mechanisms";
	public static final boolean GENERATE_CONCURRENCY_MECHANISMS_DEFAULT = false;

	public static final String CLASSES_TO_SKIP = PLUGIN_ID + ".classes_to_skip";
	public static final String CLASSES_TO_SKIP_DEFAULT = "";
	
	public static final String JAVA_PACKAGE = PLUGIN_ID + ".java_package";
	public static final String JAVA_PACKAGE_DEFAULT = "";
	
	public static final String GENERATE_JML = PLUGIN_ID + ".jml";
	public static final boolean GENERATE_JML_DEFAULT = false;
	
	public static final String JML_USE_INVARIANT_FOR = PLUGIN_ID + ".jml_use_invariant_for";
	public static final boolean JML_USE_INVARIANT_FOR_DEFAULT = false;

}
