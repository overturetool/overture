package org.overture.vdmj.typechecker.tests.external;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.overturetool.test.framework.BaseTestSuite;
import org.overturetool.test.framework.Properties;

public class ExternalClassesPpTypeCheckTest extends BaseTestSuite
{
	public static Test suite() throws IllegalArgumentException,
			SecurityException, InstantiationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException, IOException
	{
		Properties.recordTestResults = true;
		String name = "Type_Check_PP_Classes_TestSuite_External";
		String root = ExternalTestSettings.basePath + "pptest/tc";
		TestSuite test = null;
		if (new File(root).exists())
		{
			test = createTestCompleteFile(name, root, NewClassTestCase.class);
		} else
		{
			test = new TestSuite("Empty Skipped Test Suite");
		}
		return test;
	}
}
