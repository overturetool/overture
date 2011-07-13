package org.overture.parser.tests;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Enumeration;

import org.overture.parser.tests.framework.BaseTestSuite;

import junit.framework.Test;
import junit.framework.TestSuite;

public class DefinitionTestSuite extends BaseTestSuite
{

	public static Test suite() throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, IOException
	{
		String name = "Parser Definition TestSuite";
		String root = "src\\test\\resources\\definitions";
		TestSuite test = createTestCompleteFile(name,new File(root+"\\complete"),DefinitionTestCase.class);
		TestSuite test2 = createTestSingleLineFile(name,new File(root+"\\singleline"),DefinitionTestCase.class);
		@SuppressWarnings("unchecked")
		Enumeration<Test> e = test2.tests();
		while(e.hasMoreElements())
		{
			test.addTest(e.nextElement());
		}
		return test;
	}
	
	
	
}
