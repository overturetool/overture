package org.overture.parser.tests;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import junit.framework.Test;

public class ExpressionTestSuite extends BaseTestSuite
{

	public static Test suite() throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException
	{
		String name = "Parser Expression TestSuite";
		String root = "src\\test\\resources\\expressions";
		return createTestCompleteFile(name,new File(root),ExpressionTestCase.class);
	}
	
	
	
}
