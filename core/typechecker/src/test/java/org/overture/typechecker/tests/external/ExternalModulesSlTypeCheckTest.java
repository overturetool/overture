package org.overture.typechecker.tests.external;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.overturetool.test.framework.BaseTestSuite;
import org.overturetool.test.framework.ResultTestCase;

public class ExternalModulesSlTypeCheckTest extends BaseTestSuite
{
	public static Test suite() throws IllegalArgumentException,
			SecurityException, InstantiationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException, IOException
	{
		
		String name = "Type_Check_SL_Modules_TestSuite_External";
		String root = ExternalTestSettings.basePath+"sltest/tc";
		TestSuite test = null;
		if (new File(root).exists())
		{
			test = createTestCompleteFile(name, root, NewModuleTestCase.class);
		} else
		{
			test = new TestSuite("Empty Skipped Test Suite");
		}
		return test;
	}
}
