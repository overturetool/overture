package org.overture.intepreter.tests.external;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.overturetool.vdmj.lex.LexLocation;
import org.overture.test.framework.BaseTestSuite;
import org.overture.test.framework.Properties;

public class ExternalClassesPpTypeCheckTest extends BaseTestSuite
{
	public static Test suite() throws IllegalArgumentException,
			SecurityException, InstantiationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException, IOException
	{
		LexLocation.absoluteToStringLocation = false;
		Properties.recordTestResults = true;
		String name = "Intepreter_PP_Classes_TestSuite_External";
		File root = ExternalTestSettings.getBasePath("pptest/cgip");
		TestSuite test = null;
		if (root != null && root.exists())
		{
			test = createTestCompleteFile(name, root.getAbsolutePath(), NewClassTestCase.class);
		} else
		{
			test = new TestSuite("Empty Skipped Test Suite");
		}
		return test;
	}
}
