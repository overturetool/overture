package org.overture.codegen.constants;

import org.apache.commons.lang.ArrayUtils;

public class OoAstConstants
{
	public static final String PRIVATE = "private";
	public static final String PUBLIC = "public";
	
	public static final String CONSTRUCTOR_FORMAL_PREFIX = "_";
	
	public static final String QUOTES_INTERFACE_NAME = "Quotes";
	
	public static final String IO_CLASS_NAME = "IO";
	public static final String MATH_CLASS_NAME = "MATH";
	public static final String UTIL_CLASS_NAME = "VDMUtil";
	
	public static final String UTIL_RESOURCE_FOLDER = "lib";
	
	public static final String[] UTIL_NAMES = {IO_CLASS_NAME, MATH_CLASS_NAME, UTIL_CLASS_NAME};

	public static final String[] CLASS_NAMES_USED_IN_VDM = {
		"CSV",
		"IO",
		"MATH",
		"VDMUnit",
		"Throwable",
		"Error",
		"AssertionFailedError",
		"Assert",
		"Test",
		"TestCase",
		"TestSuite",
		"TestListener",
		"TestResult",
		"TestRunner",
		"VDMUtil",
		"CPU",
		"BUS"};
	
	public static final String[] RESERVED_CLASS_NAMES = (String[]) ArrayUtils.addAll(new String[]{QUOTES_INTERFACE_NAME}, CLASS_NAMES_USED_IN_VDM);
	
}
