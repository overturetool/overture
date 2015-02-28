package org.overture.ct.ctruntime.tests;

import java.io.File;
import java.util.Collection;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.overture.ast.lex.Dialect;
import org.overture.config.Release;
import org.overture.config.Settings;
import org.overture.ct.ctruntime.tests.util.TestSourceFinder;
import org.overture.ct.ctruntime.utils.CtHelper.CtTestData;

@RunWith(value = Parameterized.class)
public class CtNoReductionPpTestCase extends CtTestCaseBase
{
	private static String TEST_NAME = "CT no reduction PP tests";
	private static final String ROOT = "src/test/resources/no_reduction_pp_specs";

	@Parameters(name = "{0}")
	public static Collection<Object[]> getData()
	{
		Collection<Object[]> tests = TestSourceFinder.createTestCompleteFile(Dialect.VDM_PP, TEST_NAME, ROOT, "", "");

		return tests;
	}

	public CtNoReductionPpTestCase(String name, File file, File traceFolder,
			CtTestData args)
	{
		super(file, traceFolder, args);
	}
	
	@Override
	public void setUp() throws Exception
	{
		Settings.dialect = Dialect.VDM_PP;
		Settings.release = Release.VDM_10;
	}
	
	@Override
	protected String getPropertyId()
	{
		return "pp.no";
	}

}
