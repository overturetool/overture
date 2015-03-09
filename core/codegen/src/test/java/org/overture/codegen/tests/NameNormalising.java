package org.overture.codegen.tests;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.overture.ast.lex.LexLocation;
import org.overture.test.framework.BaseTestSuite;
import org.overture.test.framework.Properties;

public class NameNormalising extends BaseTestSuite
{
	public static final String ROOT = "src" + File.separatorChar + "test"
			+ File.separatorChar + "resources" + File.separatorChar + "renaming_specs";

	public static Test suite() throws IllegalArgumentException,
			SecurityException, InstantiationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException, IOException
	{
		LexLocation.absoluteToStringLocation = false;
		Properties.recordTestResults = TestFlags.NAME_NORMALISING_TESTS_ON;

		String name = "Name normalising test case";
		TestSuite test = createTestCompleteFile(name, ROOT, SpecificationTestCase.class, "");
		return test;
	}
}
