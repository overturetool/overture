package org.overture.parser.tests;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.overturetool.test.framework.BaseTestSuite;

import junit.framework.Test;


public class SpecificationPpTestSuite extends BaseTestSuite
{

	public static Test suite() throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, IOException
	{
		String name = "Parser Specification PP TestSuite";
		//String root = "src\\test\\resources\\specifications\\pp";
		String root = "src\\test\\resources\\test";
		return createTestCompleteFile(name,root,SpecificatopnPpTestCase.class,"");
	}
}
