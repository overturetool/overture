/*
 * #%~
 * VDM Code Generator
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
package org.overture.codegen.merging;

import java.io.File;

public class TemplateStructure
{
	public static final String TEMPLATE_FILE_EXTENSION = ".vm";

	private String root;

	public final String DECL_PATH;
	public final String LOCAL_DECLS_PATH;

	public final String STM_PATH;

	public final String EXP_PATH;
	public final String BINARY_EXP_PATH;
	public final String IS_EXP_PATH;
	public final String NUMERIC_BINARY_EXP_PATH;
	public final String BOOL_BINARY_EXP_PATH;
	public final String UNARY_EXP_PATH;
	public final String SEQ_EXP_PATH;
	public final String SET_EXP_PATH;
	public final String MAP_EXP_PATH;
	public final String QUANTIFIER_EXP_PATH;
	public final String RUNTIME_ERROR_EXP_PATH;

	public final String TYPE_PATH;
	public final String BASIC_TYPE_PATH;
	public final String BASIC_TYPE_WRAPPERS_PATH;
	public final String SET_TYPE_PATH;
	public final String SEQ_TYPE_PATH;
	public final String MAP_TYPE_PATH;

	public final String STATE_DESIGNATOR_PATH;
	public final String OBJECT_DESIGNATOR_PATH;
	public final String PATTERN_PATH;

	public TemplateStructure(String rootFolder)
	{
		root = rootFolder + File.separatorChar;

		DECL_PATH = root + "Declarations" + File.separatorChar;
		LOCAL_DECLS_PATH = root + "LocalDecls" + File.separatorChar;

		STM_PATH = root + "Statements" + File.separatorChar;

		EXP_PATH = root + "Expressions" + File.separatorChar;
		BINARY_EXP_PATH = EXP_PATH + "Binary" + File.separatorChar;
		IS_EXP_PATH = EXP_PATH + "Is" + File.separatorChar;
		NUMERIC_BINARY_EXP_PATH = BINARY_EXP_PATH + "Numeric"
				+ File.separatorChar;
		BOOL_BINARY_EXP_PATH = BINARY_EXP_PATH + "Bool" + File.separatorChar;
		UNARY_EXP_PATH = EXP_PATH + "Unary" + File.separatorChar;
		SEQ_EXP_PATH = EXP_PATH + "Seq" + File.separatorChar;
		SET_EXP_PATH = EXP_PATH + "Set" + File.separatorChar;
		MAP_EXP_PATH = EXP_PATH + "Map" + File.separatorChar;
		QUANTIFIER_EXP_PATH = EXP_PATH + "Quantifier" + File.separatorChar;
		RUNTIME_ERROR_EXP_PATH = EXP_PATH + "RuntimeError" + File.separatorChar;

		TYPE_PATH = root + "Types" + File.separatorChar;
		BASIC_TYPE_PATH = TYPE_PATH + "Basic" + File.separatorChar;
		BASIC_TYPE_WRAPPERS_PATH = TYPE_PATH + "BasicWrappers"
				+ File.separatorChar;
		SET_TYPE_PATH = TYPE_PATH + "Set" + File.separatorChar;
		SEQ_TYPE_PATH = TYPE_PATH + "Seq" + File.separatorChar;
		MAP_TYPE_PATH = TYPE_PATH + "Map" + File.separatorChar;

		STATE_DESIGNATOR_PATH = root + "StateDesignator" + File.separatorChar;
		OBJECT_DESIGNATOR_PATH = root + "ObjectDesignator" + File.separatorChar;
		PATTERN_PATH = root + "Pattern" + File.separatorChar;
	}

	public String makePath(String path)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(root);
		sb.append(path);
		sb.append(File.separatorChar);
		return sb.toString();
	}
}
