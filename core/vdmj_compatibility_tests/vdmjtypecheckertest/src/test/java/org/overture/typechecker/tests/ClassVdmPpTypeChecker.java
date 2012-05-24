package org.overture.typechecker.tests;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.overture.typechecker.tests.framework.BaseTestSuite;
import org.overture.typechecker.tests.framework.ClassTestCase;

public class ClassVdmPpTypeChecker extends BaseTestSuite {
	
	public static Test suite() throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, IOException
	{
		String name = "Type Check Class TestSuite";
		String root = "src\\test\\resources\\classesPP";
		TestSuite test = createTestCompleteFile(name, root, ClassTestCase.class);
		return test;
	}
}
