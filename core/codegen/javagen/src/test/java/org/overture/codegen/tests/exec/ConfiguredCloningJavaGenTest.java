package org.overture.codegen.tests.exec;

import java.io.File;
import java.util.Collection;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.overture.ast.lex.Dialect;
import org.overture.codegen.execution.tests.CommonJavaGenCheckerTest;
import org.overture.codegen.tests.ConfiguredCloningTest;
import org.overture.config.Release;

@RunWith(value = Parameterized.class)
public class ConfiguredCloningJavaGenTest extends CommonJavaGenCheckerTest
{

	public ConfiguredCloningJavaGenTest(String name,File vdmSpec, File javaGeneratedFiles,
			TestHandler testHandler, String rootPackage)
	{
		super(vdmSpec, javaGeneratedFiles, testHandler, rootPackage);
	}

	@Parameters(name = "{0}")
	public static Collection<Object[]> getData()
	{
		return collectTests(new File(ConfiguredCloningTest.ROOT),new ExecutableSpecTestHandler(Release.CLASSIC, Dialect.VDM_PP));
	}

	@Override
	protected String getPropertyId()
	{
		return "configuredcloning";
	}

}
