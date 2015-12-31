package org.overture.vdm2jml.tests;

import java.io.File;
import java.util.Collection;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.overture.core.tests.PathsProvider;

@RunWith(Parameterized.class)
public class JmlReuseSlTraceOutputTest extends JmlSlOutputTestBase
{
	public static final String ROOT = "src" + File.separatorChar + "test" + File.separatorChar + "resources"
			+ File.separatorChar + "dynamic_analysis" + File.separatorChar + "traces_sl_copies";

	public JmlReuseSlTraceOutputTest(String nameParameter, String inputParameter, String resultParameter)
	{
		super(nameParameter, inputParameter, resultParameter);
	}
	
	@Parameters(name = "{index} : {0}")
	public static Collection<Object[]> testData()
	{
		return PathsProvider.computePaths(ROOT);
	}
	
	@Override
	protected String getUpdatePropertyString()
	{
		return JML_SL_TRACE_UPDATE_PROPERTY + "reuse";
	}
}
