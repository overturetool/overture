package org.overture.interpreter.tests;

import java.io.File;
import java.util.Collection;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.overture.ast.lex.Dialect;

@RunWith(value = Parameterized.class)
public class ClassesPpTest extends CommonInterpreterTest
{

	public ClassesPpTest(Dialect dialect, String suiteName, File testSuiteRoot,
			File file)
	{
		super(dialect, file, suiteName, testSuiteRoot);
	}

	@Parameters(name = "{1}")
	public static Collection<Object[]> getData()
	{
		String name = "Interpreter Class PP TestSuite";
		String root = "src\\test\\resources\\classes";

		Collection<Object[]> tests = TestSourceFinder.createTestCompleteFile(Dialect.VDM_PP, name, root, "vdmpp", "");
		return tests;
	}

	@Override
	protected String getPropertyId()
	{
		return "class.pp";
	}

}
