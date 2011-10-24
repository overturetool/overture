package org.overturetool.test.examples.testsuites;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Enumeration;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.overturetool.test.examples.ParserPpTestCase;
import org.overturetool.test.examples.ParserRtTestCase;
import org.overturetool.test.examples.ParserSlTestCase;
import org.overturetool.test.framework.BaseTestSuite;


public class VdmParserExamplesTestSuite extends BaseTestSuite
{
	public static Test suite() throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, IOException
	{
		String name = "Parser VDM++ Examples TestSuite";
		String root = "../../documentation/examples/";
		TestSuite test = createTestCompleteDirectory(name,root+"VDM++",ParserPpTestCase.class);
		add(test,createTestCompleteDirectory(name,root+"VDMSL",ParserSlTestCase.class));
		add(test,createTestCompleteDirectory(name,root+"VDMRT",ParserRtTestCase.class));
		return test;
	}

	private static void add(TestSuite test, TestSuite test2)
	{
		@SuppressWarnings("unchecked")
		Enumeration<Test> e = test2.tests();
		while(e.hasMoreElements())
		{
			test.addTest(e.nextElement());
		}
	}
	
	
}
