package org.overture.codegen.merging;

import org.overture.codegen.constants.IText;

public class TemplateStructure
{
	public static final String NEW_LINE = System.getProperty("line.separator");
	
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
		root = rootFolder + IText.SEPARATOR_CHAR;
		DECL_PATH = root + "Declarations" + IText.SEPARATOR_CHAR;
		LOCAL_DECLS_PATH = "LocalDecls" + IText.SEPARATOR_CHAR;
		STM_PATH = root + "Statements" + IText.SEPARATOR_CHAR;
		EXPS_PATH = root + "Expressions" + IText.SEPARATOR_CHAR;
		BINARY_EXPS_PATH = EXPS_PATH + "Binary" + IText.SEPARATOR_CHAR;
		NUMERIC_BINARY_EXPS_PATH = BINARY_EXPS_PATH + "Numeric" + IText.SEPARATOR_CHAR;
		UNARY_EXPS_PATH = EXPS_PATH + "Unary" + IText.SEPARATOR_CHAR;
		SEQ_EXPS_PATH = EXPS_PATH + "Seq" + IText.SEPARATOR_CHAR;
		TYPE_PATH = root + "Types" + IText.SEPARATOR_CHAR;
		
		BASIC_TYPE_PATH = TYPE_PATH + "Basic" + IText.SEPARATOR_CHAR;
		BASIC_TYPE_WRAPPERS_PATH = TYPE_PATH + "BasicWrappers" + IText.SEPARATOR_CHAR;
		SET_TYPE_PATH = TYPE_PATH + "Set" + IText.SEPARATOR_CHAR;
		SEQ_TYPE_PATH = TYPE_PATH + "Seq" + IText.SEPARATOR_CHAR;
		STATE_DESIGNATOR_PATH = root + "StateDesignator" + IText.SEPARATOR_CHAR; 
	}
	
}
