package org.overture.pog.tests;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.overture.pog.tests.framework.BaseTestSuite;
import org.overture.pog.tests.framework.ModuleTestCase;

public class ModulesVDMSlVDMJPoGTest extends BaseTestSuite
{

	private enum TestSuites
	{
		FUNCTIONAL("functional_tests"), EXPRESSIONS("expressions"), STATEMENTS(
				"statements"), THIRDPARTY("thirdpartytests");
		private String folder;

		TestSuites(String folder)
		{
			this.folder = folder;
		}

		public String getFolder()
		{
			return this.folder;
		}
	}

	private static Test getSuite(TestSuites suite)
			throws IllegalArgumentException, SecurityException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException, IOException
	{
		String name = "Type Check Module TestSuite";
		String root = "src\\test\\resources\\" + suite.getFolder();
		TestSuite test = createTestCompleteFile(name, root, ModuleTestCase.class);
		return test;
	}

	private static Test getSuite(AbstractTests suite)
			throws IllegalArgumentException, SecurityException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException, IOException
	{
		String name = "Type Check Module TestSuite";
		String root = "src\\test\\resources\\" + suite.getFolder();
		TestSuite test = createTestCompleteFile(name, root, ModuleTestCase.class);
		return test;
	}

	public static Test suite() throws Exception
	{
		TestSuite t = new TestSuite();
		// t.addTest(getSuite(ThirdPartyTests.recfun_40$vdm));
		// t.addTest(getSuite(FunctionalTests.newspeakSL));
		t.addTest(getSuite(TestSuites.THIRDPARTY));
		t.addTest(getSuite(TestSuites.STATEMENTS));
		t.addTest(getSuite(TestSuites.FUNCTIONAL));
		t.addTest(getSuite(TestSuites.EXPRESSIONS));
		return t;
	}
}
