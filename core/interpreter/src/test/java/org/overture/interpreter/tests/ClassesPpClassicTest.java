package org.overture.interpreter.tests;

import java.io.File;
import java.util.Collection;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.overture.ast.lex.Dialect;
import org.overture.config.Release;
import org.overture.config.Settings;
import org.overture.interpreter.tests.utils.TestSourceFinder;

@RunWith(value = Parameterized.class)
public class ClassesPpClassicTest extends CommonInterpreterTest
{

	public ClassesPpClassicTest(Dialect dialect, String suiteName,
			File testSuiteRoot, File file)
	{
		super(dialect, file, suiteName, testSuiteRoot);
	}

	@Parameters(name = "{1}")
	public static Collection<Object[]> getData()
	{
		String name = "Interpreter Class PP TestSuite";
		String root = "src/test/resources/classesClassic/";

		Collection<Object[]> tests = TestSourceFinder.createTestCompleteFile(Dialect.VDM_PP, name, root, "vpp", "");
		return tests;
	}

	@Override
	protected String getPropertyId()
	{
		return "class-classic.pp";
	}

	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		Settings.release = Release.CLASSIC;
	}

}
