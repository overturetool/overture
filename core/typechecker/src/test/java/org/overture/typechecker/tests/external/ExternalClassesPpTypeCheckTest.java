package org.overture.typechecker.tests.external;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.overture.typechecker.tests.framework.BaseTestSuite;
import org.overture.typechecker.tests.framework.ClassTestCase;

public class ExternalClassesPpTypeCheckTest extends BaseTestSuite
{
	public static Test suite() throws IllegalArgumentException,
			SecurityException, InstantiationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException, IOException
	{
		String name = "Type Check Classes TestSuite with external data";
		String root = "/home/jenkins/resources/protected/externalTests/pptest/tc";
		TestSuite test = null;
		if (new File(root).exists())
		{
			test = createTestCompleteFile(name, root, ClassTestCase.class);
		} else
		{
			test = new TestSuite("Empty Skipped Test Suite");
		}
		return test;
	}
}
