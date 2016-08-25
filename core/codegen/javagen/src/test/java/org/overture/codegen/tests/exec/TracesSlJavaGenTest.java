package org.overture.codegen.tests.exec;

import java.io.File;
import java.util.Collection;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.overture.ast.lex.Dialect;
import org.overture.codegen.ir.IRSettings;
import org.overture.codegen.tests.exec.base.JavaGenTestBase;
import org.overture.codegen.tests.exec.util.testhandlers.TestHandler;
import org.overture.codegen.tests.exec.util.testhandlers.TraceHandler;
import org.overture.codegen.tests.output.TracesSlOutputTest;
import org.overture.config.Release;

@RunWith(value = Parameterized.class)
public class TracesSlJavaGenTest extends JavaGenTestBase
{
	public TracesSlJavaGenTest(String name, File vdmSpec,
			TestHandler testHandler)
	{
		super(vdmSpec, testHandler);
	}

	@Parameters(name = "{0}")
	public static Collection<Object[]> getData()
	{
		return collectTests(new File(TracesSlOutputTest.ROOT), new TraceHandler(Release.VDM_10, Dialect.VDM_SL));
	}

	@Override
	public IRSettings getIrSettings()
	{
		IRSettings irSettings = super.getIrSettings();
		irSettings.setGenerateTraces(true);
		irSettings.setGeneratePreCondChecks(true);
		irSettings.setGeneratePreConds(true);
		irSettings.setMakePreCondsPublic(true);
		irSettings.setMakePostCondsPublic(true);

		return irSettings;
	}

	@Override
	protected String getPropertyId()
	{
		return "tracessl";
	}
}
