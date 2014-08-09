package org.overture.ct.ctruntime.tests;

import java.io.File;
import java.util.Collection;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.overture.ast.lex.Dialect;
import org.overture.ct.ctruntime.tests.util.TestSourceFinder;
import org.overture.test.framework.Properties;

@RunWith(value = Parameterized.class)
public class CtNoReductionTestCase extends CtTestCaseBase
{
	private static String TEST_NAME = "CT tests";
	private static final String ROOT = "src/test/resources/no_reduction_sl_specs";

	@Parameters(name = "{0}")
	public static Collection<Object[]> getData()
	{
		Properties.recordTestResults = false;
		
		Collection<Object[]> tests = TestSourceFinder.createTestCompleteFile(Dialect.VDM_PP, TEST_NAME, ROOT, "", "");

		return tests;
	}

	public CtNoReductionTestCase(String name, File file, File traceFolder,
			String[] args)
	{
		super(file, traceFolder, args);
	}

}
