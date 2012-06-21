package org.overture.interpreter.tests.external;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.overturetool.test.framework.BaseTestSuite;

public class ExternalModulesSlInterpreterTestSuite extends BaseTestSuite
{
	public static Test suite() throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, IOException
	{
		org.overturetool.test.framework.Properties.recordTestResults = true;
		String name = "Interpreter SL Modules TestSuite External";
		File root = getBasePath("sltest/cgip");
		TestSuite test = null;
		if (root != null && root.exists())
		{
			test = createTestCompleteFile(name, root.getAbsolutePath(), ExternalInterpreterSlTestCase.class);
		} else
		{
			test = new TestSuite("Empty Skipped Test Suite");
		}
		return test;
	}
	
	public static File getBasePath(String string)
	{
		String path = System.getProperty("externalTestsPath"); 
		if(path != null)
		{	
			File f = new File(new File(path), string);			
			return f;
		}
		else
		{
			System.out.println("ExternalTestsPath not found");
			return null;
		}
	}
}
