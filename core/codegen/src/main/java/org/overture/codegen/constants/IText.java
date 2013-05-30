package org.overture.codegen.constants;

import java.io.File;

public class IText
{
	public final static char SEPARATOR_CHAR = File.separatorChar;
	
	public static final String TEMPLATE_FILE_EXTENSION = ".vm";

	public static final String ROOT = "Templates" + SEPARATOR_CHAR;
	
	public static final String DECL_PATH = ROOT + "Declarations" + SEPARATOR_CHAR;
	
	public static final String TYPE_DECLS_PATH = ROOT + "TypeDeclarations" + SEPARATOR_CHAR;
	
	public static final String LOCAL_DECLS_PATH = "LocalDecls" + SEPARATOR_CHAR;
	
	public static final String STM_PATH = ROOT + "Statements" + SEPARATOR_CHAR;
	
	public static final String EXPS_PATH = ROOT + "Expressions" + SEPARATOR_CHAR;
	
	public static final String BINARY_EXPS_PATH = EXPS_PATH + "Binary" + SEPARATOR_CHAR;
	
	public static final String NUMERIC_BINARY_EXPS_PATH = BINARY_EXPS_PATH + "Numeric" + SEPARATOR_CHAR;
	
	public static final String UNARY_EXPS_PATH = EXPS_PATH + "Unary" + SEPARATOR_CHAR;

	public static final String SEQ_EXPS_PATH = EXPS_PATH + "Seq" + SEPARATOR_CHAR;
	
	public static final String TYPE_PATH = ROOT + "Types" + SEPARATOR_CHAR;
	
	//TODO: Name it basic instead of basic type
	public static final String BASIC_TYPE_PATH = TYPE_PATH + "BasicType" + SEPARATOR_CHAR;
	
	public static final String SEQ_TYPE_PATH = TYPE_PATH + "Seq" + SEPARATOR_CHAR;
	
	public static final String STATE_DESIGNATOR_PATH = ROOT + "StateDesignator" + SEPARATOR_CHAR; 
	
}
