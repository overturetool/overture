package org.overture.pog.tests;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.overture.pog.tests.framework.ModuleSlPoTestCase;
import org.overturetool.test.framework.BaseTestSuite;
import org.overturetool.vdmj.lex.LexLocation;

public class ModulesSlPogTestSuite extends BaseTestSuite
{
	public static Test suite() throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, IOException
	{
		LexLocation.absoluteToStringLocation = false;
		String name = "POG Modules TestSuite";
		String root = "src\\test\\resources\\modules";
		TestSuite test =  createTestCompleteFile(name, root, ModuleSlPoTestCase.class);
		return test;
	}
}
