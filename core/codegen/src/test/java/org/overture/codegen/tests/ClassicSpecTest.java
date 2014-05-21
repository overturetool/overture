package org.overture.codegen.tests;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.overture.ast.lex.LexLocation;
import org.overture.test.framework.BaseTestSuite;
import org.overture.test.framework.Properties;

public class ClassicSpecTest extends BaseTestSuite
{
	public static final String ROOT = "src\\test\\resources\\classic_specs";
	
	public static Test suite() throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, IOException
	{
		LexLocation.absoluteToStringLocation = false;
		Properties.recordTestResults = TestFlags.CLASSIC_SPEC_TESTS_ON;
		
		String name = "Classic specifications test case";
		TestSuite test =  createTestCompleteFile(name, ROOT, ClassicSpecTestCase.class,"");
		return test;
	}
}
