package org.overture.codegen.vdm2java;

import org.overture.codegen.constants.IText;

public interface IJavaCodeGenConstants
{
	public static final String JAVA_TEMPLATES_ROOT_FOLDER = IText.SEPARATOR_CHAR + "JavaTemplates";
	public static final String JAVA_UTILS_ROOT_FOLDER = IText.SEPARATOR_CHAR + "JavaUtils";
	public static final String JAVA_FILE_EXTENSION = ".java";
	public static final String UTILS_PACKAGE = "package utils;";
	
	public static final String UTILS_FILE = "Utils";
	
	public static final String MATH_FILE = "MATH";
	public static final String IO_FILE = "IO";
	public static final String VDM_UTIL_FILE = "VDMUtil";
	
	public static final String[] CLASSES_NOT_TO_BE_GENERATED = {VDM_UTIL_FILE, MATH_FILE, IO_FILE};
}
