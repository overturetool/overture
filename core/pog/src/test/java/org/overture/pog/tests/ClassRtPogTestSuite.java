package org.overture.pog.tests;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.overture.pog.tests.framework.ClassRtPoTestCase;
import org.overturetool.test.framework.BaseTestSuite;

public class ClassRtPogTestSuite extends BaseTestSuite
{
	public static Test suite() throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, IOException
	{
//		Properties.recordTestResults = true;
		String name = "POG Classes PP TestSuite";
		String root = "src\\test\\resources\\classesRT";
		TestSuite test =  createTestCompleteFile(name, root, ClassRtPoTestCase.class);
		return test;
	}
}
