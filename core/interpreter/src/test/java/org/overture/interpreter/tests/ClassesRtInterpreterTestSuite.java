package org.overture.interpreter.tests;

import java.io.File;
import java.util.Collection;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.overture.ast.lex.Dialect;
import org.overture.ast.lex.LexLocation;
import org.overture.interpreter.tests.utils.TestSourceFinder;

@RunWith(value = Parameterized.class)
public class ClassesRtInterpreterTestSuite extends InterpreterStringSlTestCase
{
	// public static Test suite() throws IllegalArgumentException, SecurityException, InstantiationException,
	// IllegalAccessException, InvocationTargetException, NoSuchMethodException, IOException
	// {
	// org.overture.test.framework.Properties.recordTestResults = false;
	// String name = "Interpreter Class RT TestSuite";
	// String root = "src\\test\\resources\\classesRT";
	// TestSuite test = createTestCompleteFile(name, root, InterpreterStringRtTestCase.class,"vpp","");
	// return test;
	// }

	public ClassesRtInterpreterTestSuite(Dialect dialect, String suiteName,
			File testSuiteRoot, File file)
	{
		super(dialect, file, suiteName, testSuiteRoot);
	}

	@Parameters(name = "{1}")
	public static Collection<Object[]> getData()
	{
		LexLocation.absoluteToStringLocation = false;
		org.overture.test.framework.Properties.recordTestResults = false;
		String name = "Interpreter Class RT TestSuite";
		String root = "src\\test\\resources\\classesRT";

		Collection<Object[]> tests = TestSourceFinder.createTestCompleteFile(Dialect.VDM_RT, name, root, "vpp", "");
		return tests;
	}

}
