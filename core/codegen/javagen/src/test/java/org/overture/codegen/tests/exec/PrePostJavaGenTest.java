package org.overture.codegen.tests.exec;

import java.io.File;
import java.util.Collection;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.overture.ast.lex.Dialect;
import org.overture.codegen.execution.tests.CommonJavaGenCheckerTest;
import org.overture.codegen.ir.IRSettings;
import org.overture.codegen.tests.PrePostTest;
import org.overture.config.Release;

@RunWith(value = Parameterized.class)
public class PrePostJavaGenTest extends CommonJavaGenCheckerTest
{
	public PrePostJavaGenTest(String name, File vdmSpec, TestHandler testHandler)
	{
		super(vdmSpec, testHandler);
	}

	@Parameters(name = "{0}")
	public static Collection<Object[]> getData()
	{
		return collectTests(new File(PrePostTest.ROOT),new ExecutableSpecTestHandler(Release.VDM_10, Dialect.VDM_PP));
	}

	@Override
	public IRSettings getIrSettings()
	{
		IRSettings irSettings = new IRSettings();

		irSettings.setGeneratePreConds(true);
		irSettings.setGeneratePreCondChecks(true);
		irSettings.setGeneratePostConds(true);
		irSettings.setGeneratePostCondChecks(true);

		return irSettings;
	}
	
	@Override
	protected String getPropertyId()
	{
		return "prepost";
	}
}
