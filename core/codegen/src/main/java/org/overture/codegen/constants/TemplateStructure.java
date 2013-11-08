package org.overture.codegen.constants;

public class TemplateStructure
{
	public static final String NEW_LINE = System.getProperty("line.separator");
	
	public final static char SEPARATOR_CHAR = '/';
	
	public static final String TEMPLATE_FILE_EXTENSION = ".vm";
	
	private String root;
	
	public final String DECL_PATH;
	public final String LOCAL_DECLS_PATH;
	public final String STM_PATH;
	public final String EXPS_PATH;
	public final String BINARY_EXPS_PATH;
	public final String NUMERIC_BINARY_EXPS_PATH;
	public final String UNARY_EXPS_PATH;
	public final String SEQ_EXPS_PATH;
	public final String TYPE_PATH;
	
	public final String BASIC_TYPE_PATH;
	public final String BASIC_TYPE_WRAPPERS_PATH;
	public final String SET_TYPE_PATH;
	public final String SEQ_TYPE_PATH;
	public final String STATE_DESIGNATOR_PATH; 
	
	public TemplateStructure(String rootFolder)
	{
		root = SEPARATOR_CHAR + rootFolder + SEPARATOR_CHAR;
		DECL_PATH = root + "Declarations" + SEPARATOR_CHAR;
		LOCAL_DECLS_PATH = "LocalDecls" + SEPARATOR_CHAR;
		STM_PATH = root + "Statements" + SEPARATOR_CHAR;
		EXPS_PATH = root + "Expressions" + SEPARATOR_CHAR;
		BINARY_EXPS_PATH = EXPS_PATH + "Binary" + SEPARATOR_CHAR;
		NUMERIC_BINARY_EXPS_PATH = BINARY_EXPS_PATH + "Numeric" + SEPARATOR_CHAR;
		UNARY_EXPS_PATH = EXPS_PATH + "Unary" + SEPARATOR_CHAR;
		SEQ_EXPS_PATH = EXPS_PATH + "Seq" + SEPARATOR_CHAR;
		TYPE_PATH = root + "Types" + SEPARATOR_CHAR;
		
		BASIC_TYPE_PATH = TYPE_PATH + "Basic" + SEPARATOR_CHAR;
		BASIC_TYPE_WRAPPERS_PATH = TYPE_PATH + "BasicWrappers" + SEPARATOR_CHAR;
		SET_TYPE_PATH = TYPE_PATH + "Set" + SEPARATOR_CHAR;
		SEQ_TYPE_PATH = TYPE_PATH + "Seq" + SEPARATOR_CHAR;
		STATE_DESIGNATOR_PATH = root + "StateDesignator" + SEPARATOR_CHAR; 
	}
	
}
