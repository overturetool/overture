package org.overture.codegen.constants;

public interface IJavaCodeGenConstants
{
	public static final String[] RESERVED_WORDS = {
		
		//Java Keywords
		"abstract", "continue", "for", "new",
		"switch", "assert", "default", "goto", "package", "synchronized",
		"boolean", "do", "if", "private", "this", "break", "double",
		"implements", "protected", "throw", "byte", "else", "import",
		"public", "throws", "case", "enum", "instanceof", "return",
		"transient", "catch", "extends", "int", "short", "try", "char",
		"final", "interface", "static", "void", "class", "finally", "long",
		"strictfp", "volatile", "const", "float", "native", "super",
		"while"
	};
	
	public static final String PACKAGE_JAVA_KEYWORD = "package";

	public static final String JAVA_FILE_EXTENSION = ".java";
	
	public static final String GET_ITERATOR = "iterator";
	public static final String ADD_ELEMENT_TO_SET = "add";
	public static final String ADD_ELEMENT_TO_SEQ = "add";
	public static final String ADD_ELEMENT_TO_MAP = "put";
	public static final String NEXT_ELEMENT_ITERATOR = "next";
	public static final String HAS_NEXT_ELEMENT_ITERATOR = "hasNext";
	public static final String ITERATOR_TYPE = "Iterator";
	
	public static final String RUNTIME_EXCEPTION_TYPE_NAME = "RuntimeException";
	
	public static final String JAVA_TEMPLATES_ROOT_FOLDER = "JavaTemplates";
	public static final String JAVA_UTILS_ROOT_FOLDER = "JavaUtils";
	
	public static final String UTILS_PACKAGE_HEADER =  PACKAGE_JAVA_KEYWORD + " utils;";
	public static final String QUOTES_PACKAGE_NAME = "quotes";
	
	public static final String UTILS_FILE = "Utils";
	public static final String SEQ_UTIL_FILE = "SeqUtil";
	public static final String SET_UTIL_FILE = "SetUtil";
	public static final String MAP_UTIL_FILE = "MapUtil";

	public static final String SEQ_UTIL_EMPTY_SEQ_CALL = "seq";
	public static final String SET_UTIL_EMPTY_SET_CALL = "set";
	public static final String MAP_UTIL_EMPTY_MAP_CALL = "map";
	
	public static final String MATH_FILE = "MATH";
	public static final String IO_FILE = "IO";
	public static final String VDM_UTIL_FILE = "VDMUtil";
	
	public static final String[] CLASSES_NOT_TO_BE_GENERATED = IOoAstConstants.CLASS_NAMES_USED_IN_VDM;
}
