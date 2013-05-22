package org.overture.codegen.constants;

import java.io.File;

public class IText
{
	public final static char SEPARATOR_CHAR = File.separatorChar;
	
	public static final String TEMPLATE_FILE_EXTENSION = ".vm";

	public static final String ROOT = "Templates" + SEPARATOR_CHAR;

	public static final String EXPS_PATH = ROOT + "Expressions" + SEPARATOR_CHAR;
	
	public static final String BINARY_EXPS_PATH = EXPS_PATH + "Binary" + SEPARATOR_CHAR;
	
	public static final String NUMERIC_BINARY_EXPS_PATH = BINARY_EXPS_PATH + "Numeric" + SEPARATOR_CHAR;
	
	public static final String UNARY_EXPS_PATH = EXPS_PATH + "Unary" + SEPARATOR_CHAR;

	public static final String TYPE_PATH = ROOT + "Types" + SEPARATOR_CHAR;
	public static final String BASIC_TYPE_PATH = TYPE_PATH + "BasicType" + SEPARATOR_CHAR;
	
}
