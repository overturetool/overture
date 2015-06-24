package org.overture.codegen.tests.exec;

import java.io.File;
import java.util.Collection;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.overture.ast.lex.Dialect;
import org.overture.codegen.execution.tests.CommonJavaGenCheckerTest;
import org.overture.codegen.tests.PackageTest;
import org.overture.codegen.vdm2java.JavaSettings;
import org.overture.config.Release;

@RunWith(value = Parameterized.class)
public class PackageJavaGenTest extends CommonJavaGenCheckerTest
{
	private static final String JAVA_ROOT_PACKAGE = "my.model";
	
	public PackageJavaGenTest(String name,File vdmSpec, File javaGeneratedFiles,
			TestHandler testHandler, String rootPackage)
	{
		super(vdmSpec, javaGeneratedFiles, testHandler, JAVA_ROOT_PACKAGE);
	}

	@Parameters(name = "{0}")
	public static Collection<Object[]> getData()
	{
		return collectTests(new File(PackageTest.ROOT),new ExecutableSpecTestHandler(Release.VDM_10, Dialect.VDM_PP));
	}

	@Override
	public JavaSettings getJavaSettings()
	{
		JavaSettings javaSettings = super.getJavaSettings();
		javaSettings.setJavaRootPackage(JAVA_ROOT_PACKAGE);
		
		return javaSettings;
	}
	
	@Override
	protected String getPropertyId()
	{
		return "package";
	}

}
