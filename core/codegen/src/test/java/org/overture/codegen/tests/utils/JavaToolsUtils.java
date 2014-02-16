package org.overture.codegen.tests.utils;

public class JavaToolsUtils
{
	public static final String JAVA_HOME = "JAVA_HOME";
	public static final String BIN_FOLDER = "bin";
	
	public static final String CURRENT_FOLDER = ".";
	
	public static final String JAVAC = "javac";
	public static final String JAVA = "java";
	
	public static Boolean isWindows()
	{
		String osName = System.getProperty("os.name");

		return osName.toUpperCase().indexOf("Windows".toUpperCase()) > -1;
	}
}
