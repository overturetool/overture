package org.overture.codegen.tests;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.overture.ast.lex.LexLocation;
import org.overture.test.framework.BaseTestSuite;
import org.overture.test.framework.Properties;

public class FunctionValueTest extends BaseTestSuite
{
	public static final String ROOT = "src\\test\\resources\\function_value_specs";
	
	public static Test suite() throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, IOException
	{
		LexLocation.absoluteToStringLocation = false;
		Properties.recordTestResults = false;
		
		String name = "Function value test case";
		TestSuite test =  createTestCompleteFile(name, ROOT, SpecificationTestCase.class,"");
		return test;
	}
}
