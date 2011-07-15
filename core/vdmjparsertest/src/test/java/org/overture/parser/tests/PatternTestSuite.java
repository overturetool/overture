package org.overture.parser.tests;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import junit.framework.Test;

import org.overture.parser.tests.framework.BaseTestSuite;

public class PatternTestSuite extends BaseTestSuite
{

	public static Test suite() throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, IOException
	{
		String name = "Parser Pattern TestSuite";
		String root = "src\\test\\resources\\patterns";
		return createTestSingleLineFile(name,root,PatternTestCase.class);
	}
}
