package org.overture.parser.tests;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Enumeration;

import org.overture.parser.tests.framework.BaseTestSuite;

import junit.framework.Test;
import junit.framework.TestSuite;

public class StatementTestSuite extends BaseTestSuite
{

	public static Test suite() throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, IOException
	{
		String name = "Parser Statement TestSuite";
		String root = "src\\test\\resources\\statements";
		TestSuite test = createTestCompleteFile(name,new File(root+"\\complete"),StatementTestCase.class);
		TestSuite test2 = createTestSingleLineFile(name,new File(root+"\\singleline"),StatementTestCase.class);
		@SuppressWarnings("unchecked")
		Enumeration<Test> e = test2.tests();
		while(e.hasMoreElements())
		{
			test.addTest(e.nextElement());
		}
		return test;
	}
	
	
	
}
