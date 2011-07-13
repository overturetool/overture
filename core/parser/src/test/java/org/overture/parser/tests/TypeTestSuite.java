package org.overture.parser.tests;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.overture.parser.tests.framework.BaseTestSuite;

import junit.framework.Test;

public class TypeTestSuite extends BaseTestSuite
{

	public static Test suite() throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, IOException
	{
		String name = "Parser Type TestSuite";
		String root = "src\\test\\resources\\type";
		return createTestSingleLineFile(name,new File(root),TypeTestCase.class);
	}
}
