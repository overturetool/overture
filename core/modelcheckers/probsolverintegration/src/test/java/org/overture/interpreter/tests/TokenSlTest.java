package org.overture.interpreter.tests;

import java.io.File;
import java.util.Collection;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.overture.ast.lex.Dialect;

@RunWith(value = Parameterized.class)
public class TokenSlTest extends CommonInterpreterTest
{

	public TokenSlTest(Dialect dialect, String suiteName, File testSuiteRoot,
			File file)
	{
		super(dialect, file, suiteName, testSuiteRoot);
	}

	@Parameters(name = "{1}")
	public static Collection<Object[]> getData()
	{
		String root = "src/test/resources/modules/token/";

		Collection<Object[]> tests = TestSourceFinder.createTestCompleteFile(Dialect.VDM_SL, "", root, "vdmsl", "");
		return tests;
	}

	@Override
	protected String getPropertyId()
	{
		return "sltoken";
	}

}
