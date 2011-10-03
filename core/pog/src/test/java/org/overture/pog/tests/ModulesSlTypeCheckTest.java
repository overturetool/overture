package org.overture.pog.tests;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.overture.pog.tests.framework.BaseTestSuite;
import org.overture.pog.tests.framework.ModuleTestCase;

import junit.framework.Test;
import junit.framework.TestSuite;



public class ModulesSlTypeCheckTest extends BaseTestSuite
{
	public static Test suite() throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, IOException
	{
		String name = "Type Check Modules TestSuite";
		String root = "src\\test\\resources\\modules\\tc";
		TestSuite test =  createTestCompleteFile(name, root, ModuleTestCase.class);
		return test;
	}
}
