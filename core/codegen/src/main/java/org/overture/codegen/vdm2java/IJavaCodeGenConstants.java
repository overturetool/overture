package org.overture.codegen.vdm2java;

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
}
