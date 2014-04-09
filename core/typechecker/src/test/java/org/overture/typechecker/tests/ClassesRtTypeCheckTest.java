package org.overture.typechecker.tests;

import java.io.File;
import java.util.Collection;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.overture.ast.lex.Dialect;
import org.overture.typechecker.tests.framework.CommonTypeCheckerTest;
import org.overture.typechecker.tests.utils.TestSourceFinder;

@RunWith(value = Parameterized.class)
public class ClassesRtTypeCheckTest extends CommonTypeCheckerTest
{

	public ClassesRtTypeCheckTest(Dialect dialect, String suiteName,
			File testSuiteRoot, File file)
	{
		super(dialect, file, suiteName, testSuiteRoot);
	}

	@Parameters(name = "{1}")
	public static Collection<Object[]> getData()
	{
		String name = "Type Check Classes TestSuite";
		String root = "src\\test\\resources\\classesRT";

		Collection<Object[]> tests = TestSourceFinder.createTestCompleteFile(Dialect.VDM_RT, name, root, "", "");
		return tests;
	}

	@Override
	protected String getPropertyId()
	{
		return "class.rt";
	}
}
