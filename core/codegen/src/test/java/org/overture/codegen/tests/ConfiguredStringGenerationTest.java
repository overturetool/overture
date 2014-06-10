package org.overture.codegen.tests;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.overture.ast.lex.LexLocation;
import org.overture.test.framework.BaseTestSuite;
import org.overture.test.framework.Properties;

public class ConfiguredStringGenerationTest extends BaseTestSuite
{
	public static final String ROOT = "src\\test\\resources\\string_specs";
	
	public static Test suite() throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, IOException
	{
		LexLocation.absoluteToStringLocation = false;
		Properties.recordTestResults = TestFlags.STRING_CONFIG_TESTS_ON;
		
		String name = "Configured string generation test case";
		TestSuite test =  createTestCompleteFile(name, ROOT, ConfiguredStringGenerationTestCase.class,"");
		return test;
	}
}
