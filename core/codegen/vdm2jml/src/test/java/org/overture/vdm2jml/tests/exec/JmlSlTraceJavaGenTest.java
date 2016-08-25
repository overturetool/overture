package org.overture.vdm2jml.tests.exec;

import java.io.File;
import java.util.Collection;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.overture.ast.lex.Dialect;
import org.overture.codegen.tests.exec.util.testhandlers.TestHandler;
import org.overture.config.Release;
import org.overture.vdm2jml.tests.JmlSlTraceOutputTest;

@RunWith(value = Parameterized.class)
public class JmlSlTraceJavaGenTest extends JmlSlJavaGenTestBase
{
	public JmlSlTraceJavaGenTest(String name, File vdmSpec,
			TestHandler testHandler)
	{
		super(vdmSpec, testHandler);
	}

	@Parameters(name = "{0}")
	public static Collection<Object[]> getData()
	{
		return collectTests(new File(JmlSlTraceOutputTest.ROOT), new JmlTraceTestHandler(Release.VDM_10, Dialect.VDM_SL));
	}

	@Override
	protected String getPropertyId()
	{
		return "traces";
	}
}
