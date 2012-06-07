package org.overture.typechecker.tests;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.overture.typechecker.tests.framework.ModuleTestCase;
import org.overturetool.test.framework.BaseTestSuite;
import org.overturetool.vdmj.lex.LexLocation;

public class ModulesSlTypeCheckTest extends BaseTestSuite
{
	public static Test suite() throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, IOException
	{
		LexLocation.absoluteToStringLocation = false;
		String name = "Type Check Modules TestSuite";
		String root = "src\\test\\resources\\modules";
		TestSuite test =  createTestCompleteFile(name, root, ModuleTestCase.class);
		return test;
	}
}
