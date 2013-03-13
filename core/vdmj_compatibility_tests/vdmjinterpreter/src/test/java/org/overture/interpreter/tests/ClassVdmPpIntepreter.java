package org.overture.interpreter.tests;


import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.overture.interpreter.tests.framework.ClassTestCase;
import org.overture.test.framework.BaseTestSuite;
import org.overture.test.framework.Properties;
import org.overturetool.vdmj.lex.LexLocation;

public class ClassVdmPpIntepreter extends BaseTestSuite {
	
	public static Test suite() throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, IOException
	{
		LexLocation.absoluteToStringLocation = false;
		Properties.recordTestResults = false;
		String name = "Interpreter Class TestSuite";
		//String root = "src\\test\\resources\\classes";
		String root = "src\\test\\resources\\test";
		TestSuite test = createTestCompleteFile(name, root, ClassTestCase.class);
		return test;
	}
}
