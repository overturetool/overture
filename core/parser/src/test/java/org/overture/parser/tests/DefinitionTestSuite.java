package org.overture.parser.tests;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Enumeration;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.overture.parser.tests.framework.BaseTestSuite;

public class DefinitionTestSuite extends BaseTestSuite
{

	public static Test suite() throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, IOException
	{
		String name = "Parser Definition TestSuite";
		String root = "src\\test\\resources\\definitions";
		TestSuite test = createTestCompleteFile(name,root+"\\complete",DefinitionTestCase.class);
		TestSuite test2 = createTestSingleLineFile(name,root+"\\singleline",DefinitionTestCase.class);
		@SuppressWarnings("unchecked")
		Enumeration<Test> e = test2.tests();
		while(e.hasMoreElements())
		{
			test.addTest(e.nextElement());
		}
		return test;
	}
	
	
	
}
