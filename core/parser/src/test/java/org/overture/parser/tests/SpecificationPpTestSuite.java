package org.overture.parser.tests;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import junit.framework.Test;

import org.overture.parser.tests.framework.BaseTestSuite;

public class SpecificationPpTestSuite extends BaseTestSuite
{

	public static Test suite() throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, IOException
	{
		String name = "Parser Specification PP TestSuite";
		//String root = "src\\test\\resources\\specifications\\pp";
		String root = "src\\test\\resources\\test";
		return createTestCompleteFile(name,root,SpecificatopnPpTestCase.class);
	}
}
