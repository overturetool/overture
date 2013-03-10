package org.overture.intepreter.tests.external;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.overture.test.framework.BaseTestSuite;
import org.overture.test.framework.Properties;
import org.overturetool.vdmj.lex.LexLocation;

public class ExternalModuleSlTypeCheckTest extends BaseTestSuite
{
	public static Test suite() throws IllegalArgumentException,
			SecurityException, InstantiationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException, IOException
	{
		LexLocation.absoluteToStringLocation = false;
		Properties.recordTestResults = true;
		String name = "Interpreter_SL_Modules_TestSuite_External";
		File root = ExternalTestSettings.getBasePath("sltest/cgip");
		TestSuite test = null;
		if (root != null && root.exists())
		{
			test = createTestCompleteFile(name, root.getAbsolutePath(), NewModuleTestCase.class);
		} else
		{
			test = new TestSuite("Empty Skipped Test Suite");
		}
		return test;
	}
}
