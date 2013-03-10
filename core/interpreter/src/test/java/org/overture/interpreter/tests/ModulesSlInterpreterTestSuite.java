package org.overture.interpreter.tests;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.overture.ast.lex.LexLocation;
import org.overture.test.framework.BaseTestSuite;

public class ModulesSlInterpreterTestSuite extends BaseTestSuite
{
	public static Test suite() throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, IOException
	{
		LexLocation.absoluteToStringLocation = false;
		org.overture.test.framework.Properties.recordTestResults = false;
		String name = "Interpreter Modules TestSuite";
		String root = "src\\test\\resources\\modules";
		TestSuite test =  createTestCompleteFile(name, root, InterpreterStringSlTestCase.class,"vdm","");
		return test;
	}
}
