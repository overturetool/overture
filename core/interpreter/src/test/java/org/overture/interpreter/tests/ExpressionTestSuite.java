package org.overture.interpreter.tests;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Enumeration;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.overture.test.framework.BaseTestSuite;


public class ExpressionTestSuite extends BaseTestSuite
{

	public static Test suite() throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, IOException
	{
		org.overture.test.framework.Properties.recordTestResults=false;
		String name = "Interpreter Expression TestSuite";
		String root = "src\\test\\resources\\expressions";
		TestSuite test = createTestCompleteFile(name,root+"\\complete",ExpressionTestCase.class,"txt");
		TestSuite test2 = createTestSingleLineFile(name,root+"\\singleline",ExpressionTestCase.class,"");
		@SuppressWarnings("unchecked")
		Enumeration<Test> e = test2.tests();
		while(e.hasMoreElements())
		{
			test.addTest(e.nextElement());
		}
		return test;
	}
	
	
}
