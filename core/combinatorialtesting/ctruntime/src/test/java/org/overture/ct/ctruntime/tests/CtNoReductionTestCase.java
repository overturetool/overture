package org.overture.ct.ctruntime.tests;

import java.io.File;
import java.util.Collection;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.overture.ast.lex.Dialect;
import org.overture.ct.ctruntime.tests.util.TestSourceFinder;

@RunWith(value = Parameterized.class)
public class CtNoReductionTestCase extends CtTestCaseBase
{

	@Parameters(name = "{0}")
	public static Collection<Object[]> getData()
	{
		String name = "CT tests";

		String root = "src/test/resources/no_reduction_sl_specs";
		Collection<Object[]> tests = TestSourceFinder.createTestCompleteFile(Dialect.VDM_PP, name, root, "", "");

		return tests;
	}

	public CtNoReductionTestCase(String name, File file, File traceFolder,
			String[] args)
	{
		super(file, traceFolder, args);
	}

}
