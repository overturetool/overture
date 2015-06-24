package org.overture.codegen.tests.exec;

import java.io.File;
import java.util.Collection;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.overture.ast.lex.Dialect;
import org.overture.codegen.execution.tests.CommonJavaGenCheckerTest;
import org.overture.codegen.ir.IRSettings;
import org.overture.codegen.tests.RtTest;
import org.overture.config.Release;
import org.overture.config.Settings;

@RunWith(value = Parameterized.class)
public class RtJavaGenTest extends CommonJavaGenCheckerTest
{

	public RtJavaGenTest(String name, File vdmSpec, File javaGeneratedFiles,
			TestHandler testHandler, String rootPackage)
	{
		super(vdmSpec, javaGeneratedFiles, testHandler, rootPackage);
	}

	@Parameters(name = "{0}")
	public static Collection<Object[]> getData()
	{
		return collectTests(new File(RtTest.ROOT), new ExecutableSpecTestHandler(Release.VDM_10, Dialect.VDM_RT));
	}

	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		
		Settings.release = Release.VDM_10;
		Settings.dialect = Dialect.VDM_RT;
	}
	
	@Override
	public IRSettings getIrSettings()
	{
		IRSettings irSettings = new IRSettings();
		irSettings.setGenerateConc(true);

		return irSettings;
	}
	
	@Override
	protected String getPropertyId()
	{
		return "rt";
	}

}
