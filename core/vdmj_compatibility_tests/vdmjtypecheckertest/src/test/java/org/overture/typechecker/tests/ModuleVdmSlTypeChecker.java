package org.overture.typechecker.tests;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.overture.typechecker.tests.framework.BaseTestSuite;
import org.overture.typechecker.tests.framework.ModuleTestCase;
import org.overturetool.test.framework.Properties;
import org.overturetool.vdmj.lex.LexLocation;

public class ModuleVdmSlTypeChecker extends BaseTestSuite {
	
	public static Test suite() throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, IOException
	{
		LexLocation.absoluteToStringLocation = false;
		Properties.recordTestResults = true;
		String name = "Type Check Module TestSuite";
		String root = "src\\test\\resources\\modules\\";
		TestSuite test = createTestCompleteFile(name, root, ModuleTestCase.class);
		return test;
	}
}
