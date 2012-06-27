package org.overture.typechecker.tests;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.overture.ast.lex.LexLocation;
import org.overture.typechecker.tests.framework.ClassRtTestCase;
import org.overturetool.test.framework.BaseTestSuite;

public class ClassesRtTypeCheckTest extends BaseTestSuite
{
	public static Test suite() throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, IOException
	{
		LexLocation.absoluteToStringLocation = false;
		String name = "Type Check Classes TestSuite";
		String root = "src\\test\\resources\\classesRT";
		//String root = "src\\test\\resources\\test";
		TestSuite test =  createTestCompleteFile(name, root, ClassRtTestCase.class,"");
		return test;
	}
}
