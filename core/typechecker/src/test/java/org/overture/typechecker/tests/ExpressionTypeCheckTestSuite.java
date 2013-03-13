package org.overture.typechecker.tests;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.overture.test.framework.BaseTestSuite;

public class ExpressionTypeCheckTestSuite extends BaseTestSuite
{
	public static Test suite() throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, IOException
	{
//		String name = "Type Check Expression TestSuite";
//		String root = "src\\test\\resources\\expressions";
		//FIXME: have to add single line support
		TestSuite test = new TestSuite();//createTestSingleLineFile(name,root+"\\singleline",GeneratedTestCase.class);
		return test;
	}
}
