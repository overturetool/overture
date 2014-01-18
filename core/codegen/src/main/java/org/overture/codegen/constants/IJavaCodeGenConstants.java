package org.overture.codegen.constants;


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
	
	public static final String[] CLASSES_NOT_TO_BE_GENERATED = IOoAstConstants.CLASS_NAMES_USED_IN_VDM;
}
