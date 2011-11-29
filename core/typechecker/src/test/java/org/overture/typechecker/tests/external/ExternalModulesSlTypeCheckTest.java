package org.overture.typechecker.tests.external;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.overture.typechecker.tests.framework.BaseTestSuite;
import org.overture.typechecker.tests.framework.ModuleTestCase;

public class ExternalModulesSlTypeCheckTest extends BaseTestSuite
{
	public static Test suite() throws IllegalArgumentException,
			SecurityException, InstantiationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException, IOException
	{
		String name = "Type Check Modules TestSuite with external data";
		String root = "/home/jenkins/resources/protected/externalTests/sltest/tc";
		TestSuite test = null;

		if (new File(root).exists())
		{
			test = createTestCompleteFile(name, root, ModuleTestCase.class);
		} else
		{
			test = new TestSuite("Empty Skipped Test Suite");
		}
		return test;
	}
}
