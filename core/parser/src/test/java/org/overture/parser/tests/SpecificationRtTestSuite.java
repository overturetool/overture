package org.overture.parser.tests;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.overturetool.test.framework.BaseTestSuite;

import junit.framework.Test;


public class SpecificationRtTestSuite extends BaseTestSuite
{

	public static Test suite() throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, IOException
	{
		String name = "Parser Specification RT TestSuite";
		String root = "src\\test\\resources\\specifications\\rt";
		return createTestCompleteFile(name,root,SpecificatopnRtTestCase.class,"");
	}
}
