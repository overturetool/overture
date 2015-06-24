package org.overture.codegen.tests.exec;

import java.io.File;
import java.util.Collection;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.overture.ast.lex.Dialect;
import org.overture.codegen.execution.tests.CommonJavaGenCheckerTest;
import org.overture.codegen.ir.IRSettings;
import org.overture.codegen.tests.ConcurrencyClassicSpecTests;
import org.overture.config.Release;
import org.overture.config.Settings;

@RunWith(value = Parameterized.class)
public class ConcurrencyClassicJavaGenTest extends CommonJavaGenCheckerTest
{
	public ConcurrencyClassicJavaGenTest(String name, File vdmSpec,
			TestHandler testHandler)
	{
		super(vdmSpec, testHandler);
	}

	@Parameters(name = "{0}")
	public static Collection<Object[]> getData()
	{
		return collectTests(new File(ConcurrencyClassicSpecTests.ROOT),new ExecutableSpecTestHandler(Release.CLASSIC, Dialect.VDM_PP));
	}

	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		Settings.release = Release.CLASSIC;
	}
	
	@Override
	public IRSettings getIrSettings()
	{
		IRSettings irSettings = new IRSettings();
		irSettings.setGenerateConc(true);
		irSettings.setCharSeqAsString(true);
		
		return irSettings;
	}
	
	@Override
	protected String getPropertyId()
	{
		return "concurrencyclassic";
	}
}
