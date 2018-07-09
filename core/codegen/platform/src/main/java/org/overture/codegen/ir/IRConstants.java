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
package org.overture.codegen.ir;

import org.apache.commons.lang.ArrayUtils;

public class IRConstants
{
	public static final String PRIVATE = "private";
	public static final String PROTECTED = "protected";
	public static final String PUBLIC = "public";

	public static final String CONSTRUCTOR_FORMAL_PREFIX = "_";

	public static final String QUOTES_INTERFACE_NAME = "Quotes";

	public static final String TEST_CASE = "TestCase";
	public static final String TEST_CASE_RUN_FULL_SUITE = "runFullSuite";

	public static final String TEST_MODULE_NAME_POSTFIX = "Test";

	public static final String[] CLASS_NAMES_USED_IN_SL = {"CSV", "IO",  "MATH", "VDMUtil", "Assert", "TestRunner"};

	public static final String[] CLASS_NAMES_USED_IN_VDM_PP_RT =  (String[]) ArrayUtils.addAll(CLASS_NAMES_USED_IN_SL,
			new String[]{"VDMUnit", "Throwable", "Error", "AssertionFailedError",
			"Assert", "Test", "TestCase", "TestSuite", "TestListener",
			"TestResult", "TestRunner", "CPU", "BUS" });

	public static final String ILLEGAL_QUOTE_VALUE = "?";

	public static final String[] RESERVED_CLASS_NAMES_PP_RT = (String[]) ArrayUtils.addAll(new String[] {
			QUOTES_INTERFACE_NAME }, CLASS_NAMES_USED_IN_VDM_PP_RT);
}
