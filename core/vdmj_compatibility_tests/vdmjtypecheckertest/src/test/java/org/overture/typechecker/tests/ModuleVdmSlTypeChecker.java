package org.overture.typechecker.tests;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.overture.typechecker.tests.framework.BaseTestSuite;
import org.overture.typechecker.tests.framework.ModuleTestCase;

public class ModuleVdmSlTypeChecker extends BaseTestSuite {
	
	public static Test suite() throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, IOException
	{
		String name = "Type Check Module TestSuite";
		String root = "src\\test\\resources\\modules\\tc\\problem";
		TestSuite test = createTestCompleteFile(name, root, ModuleTestCase.class);
		return test;
	}
}
