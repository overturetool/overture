package org.overture.interpreter.tests.external;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.overture.ast.lex.LexLocation;
import org.overture.test.framework.BaseTestSuite;

public class ExternalClassPpInterpreterTestSuite extends BaseTestSuite
{
	public static Test suite() throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, IOException
	{
		LexLocation.absoluteToStringLocation = false;
		org.overture.test.framework.Properties.recordTestResults = false;
		String name = "Interpreter_PP_Classes_TestSuite_External";
		File root = getBasePath("pptest/cgip");
		TestSuite test = null;
		if (root != null && root.exists())
		{
			test = createTestCompleteFile(name, root.getAbsolutePath(), ExternalStringInterpreterPpTestCase.class,"vpp");
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
