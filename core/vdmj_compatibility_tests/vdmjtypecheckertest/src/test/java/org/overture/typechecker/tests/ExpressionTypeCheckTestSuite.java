package org.overture.typechecker.tests;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.overture.typechecker.tests.framework.BaseTestSuite;
//import org.overture.typechecker.tests.framework.BasicTypeCheckTestCase.ParserType;
//import org.overture.typechecker.tests.framework.GeneratedTestCase;
import org.overture.test.framework.Properties;
import org.overturetool.vdmj.lex.LexLocation;

public class ExpressionTypeCheckTestSuite extends BaseTestSuite
{
	public static Test suite() throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, IOException
	{
		LexLocation.absoluteToStringLocation = false;
		Properties.recordTestResults = false;
//		String name = "Type Check Expression TestSuite";
//		String root = "src\\test\\resources\\expressions";
//		TestSuite test = createTestSingleLineFile(ParserType.Expression,name,root+"\\singleline",GeneratedTestCase.class);
		return new TestSuite();//FIXME returned test, above, but it doesn't use the testframework properly
	}
}
