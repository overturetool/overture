package org.overture.interpreter.tests;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.overture.test.framework.BaseTestSuite;

public class ClassesRtInterpreterTestSuite extends BaseTestSuite
{
	public static Test suite() throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, IOException
	{
		org.overture.test.framework.Properties.recordTestResults = false;
		String name = "Interpreter Class RT TestSuite";
		String root = "src\\test\\resources\\classesRT";
		TestSuite test =  createTestCompleteFile(name, root, InterpreterStringRtTestCase.class,"vpp","");
		return test;
	}
}
