package org.overture.codegen.tests;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.overture.ast.lex.LexLocation;
import org.overture.test.framework.BaseTestSuite;
import org.overture.test.framework.Properties;

public class ConfiguredCloningTest extends BaseTestSuite
{
	public static final String ROOT = "src\\test\\resources\\cloning_specs";
	
	public static Test suite() throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, IOException
	{
		LexLocation.absoluteToStringLocation = false;
		Properties.recordTestResults = TestFlags.CLONING_CONFIG_TESTS_ON;
		
		String name = "Configured cloning test case";
		TestSuite test =  createTestCompleteFile(name, ROOT, ConfiguredCloningTestCase.class,"");
		return test;
	}
}
