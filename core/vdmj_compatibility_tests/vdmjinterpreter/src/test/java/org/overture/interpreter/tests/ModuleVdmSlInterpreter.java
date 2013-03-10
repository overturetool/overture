package org.overture.interpreter.tests;


import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.overture.interpreter.tests.framework.ModuleTestCase;
import org.overture.test.framework.BaseTestSuite;
import org.overture.test.framework.Properties;
import org.overturetool.vdmj.lex.LexLocation;

public class ModuleVdmSlInterpreter extends BaseTestSuite {
	
	public static Test suite() throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, IOException
	{
		LexLocation.absoluteToStringLocation = false;
		Properties.recordTestResults = true;
		String name = "Interpreter Module TestSuite";
		String root = "src\\test\\resources\\test\\";
		TestSuite test = createTestCompleteFile(name, root, ModuleTestCase.class,"vdm");
		return test;
	}
}
