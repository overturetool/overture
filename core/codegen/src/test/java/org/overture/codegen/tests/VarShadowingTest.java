package org.overture.codegen.tests;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.overture.ast.lex.LexLocation;
import org.overture.test.framework.BaseTestSuite;


public class VarShadowingTest extends BaseTestSuite
{
	
	public static final String ROOT = "src" + File.separatorChar + "test"
			+ File.separatorChar + "resources" + File.separatorChar
			+ "var_shadowing_specs";

	public static Test suite() throws IllegalArgumentException,
			SecurityException, InstantiationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException, IOException
	{
		LexLocation.absoluteToStringLocation = false;

		String name = "Var shadowing test";
		TestSuite test = createTestCompleteFile(name, ROOT, VarShadowingTestCase.class, "");
		return test;
	}
	
}
