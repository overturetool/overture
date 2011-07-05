package org.overture.parser.tests;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import junit.framework.Test;

public class PatternTestSuite extends BaseTestSuite
{

	public static Test suite() throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException
	{
		String name = "Parser Pattern TestSuite";
		String root = "src\\test\\resources\\pattern";
		return createTestCompleteFile(name,new File(root),PatternTestCase.class);
	}
}
