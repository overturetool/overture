package org.overture.interpreter.tests;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.overture.ast.lex.LexLocation;
import org.overturetool.test.framework.BaseTestSuite;

public class ClassesPpInterpreterTestSuite extends BaseTestSuite
{
	public static Test suite() throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, IOException
	{
		LexLocation.absoluteToStringLocation = false;
		org.overturetool.test.framework.Properties.recordTestResults = false;
		String name = "Interpreter Class PP TestSuite";
		String root = "src\\test\\resources\\classes";
		TestSuite test =  createTestCompleteFile(name, root, InterpreterStringPpTestCase.class,"vpp","");
		return test;
	}
}
