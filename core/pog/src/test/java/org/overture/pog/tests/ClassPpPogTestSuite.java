package org.overture.pog.tests;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.overture.ast.lex.LexLocation;
import org.overture.pog.tests.framework.ClassPpPoTestCase;
import org.overturetool.test.framework.BaseTestSuite;

public class ClassPpPogTestSuite extends BaseTestSuite
{
	public static Test suite() throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, IOException
	{
		LexLocation.absoluteToStringLocation = false;
//		Properties.recordTestResults = true;
		String name = "POG Classes PP TestSuite";
		String root = "src\\test\\resources\\classesPP";
		TestSuite test =  createTestCompleteFile(name, root, ClassPpPoTestCase.class);
		return test;
	}
}
