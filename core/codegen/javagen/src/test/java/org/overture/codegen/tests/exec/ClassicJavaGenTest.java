package org.overture.codegen.tests.exec;

import java.io.File;
import java.util.Collection;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.overture.ast.lex.Dialect;
import org.overture.codegen.execution.tests.CommonJavaGenCheckerTest;
import org.overture.codegen.tests.ClassicSpecTest;
import org.overture.config.Release;
import org.overture.config.Settings;

@RunWith(value = Parameterized.class)
public class ClassicJavaGenTest extends CommonJavaGenCheckerTest
{

	public ClassicJavaGenTest(String name,File vdmSpec, File javaGeneratedFiles,
			TestHandler testHandler, String rootPackage)
	{
		super(vdmSpec, javaGeneratedFiles, testHandler, rootPackage);
	}

	@Parameters(name = "{0}")
	public static Collection<Object[]> getData()
	{
		return collectTests(new File(ClassicSpecTest.ROOT),new ExecutableSpecTestHandler(Release.CLASSIC, Dialect.VDM_PP));
	}
	
	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		Settings.release = Release.CLASSIC;
	}

	@Override
	protected String getPropertyId()
	{
		return "classic";
	}

}
