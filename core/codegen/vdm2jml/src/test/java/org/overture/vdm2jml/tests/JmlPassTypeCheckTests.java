package org.overture.vdm2jml.tests;

import java.io.File;
import java.util.Collection;
import java.util.List;

import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.overture.codegen.utils.GeneralUtils;
import org.overture.codegen.vdm2java.JavaCodeGenUtil;
import org.overture.codegen.vdm2java.JavaToolsUtils;
import org.overture.vdm2jml.tests.util.IOpenJmlConsts;
import org.overture.vdm2jml.tests.util.ProcessResult;
import org.overture.vdm2jml.tests.util.TestUtil;

@RunWith(Parameterized.class)
public class JmlPassTypeCheckTests extends OpenJmlValidationBase
{
	public JmlPassTypeCheckTests(File inputFile)
	{
		super(inputFile);
	}

	@Parameters(name = "{index}: {0}")
	public static Collection<Object[]> data()
	{
		File folder = new File(AnnotationTestsBase.TEST_RES_STATIC_ANALYSIS_ROOT);
		List<File> files = GeneralUtils.getFiles(folder);

		return TestUtil.collectVdmslFiles(files);
	}
	
	@Before
	public void assumeTools()
	{
		Assume.assumeTrue(String.format("JML type checker test will only run if the "
				+ "property '%s' is passed", EXEC_PROPERTY), System.getProperty(EXEC_PROPERTY) != null);
		assumeOpenJml();
	}

	@Test
	public void typeCheckJml()
	{
		ProcessResult processResult = runOpenJmlProcess();
		assertNoProcessErrors(processResult);
	}

	@Override
	public String[] getProcessArgs()
	{
		String[] openJmlConfig = getTypeCheckArgs();

		String[] javaFiles = JavaCodeGenUtil.findJavaFilePathsRec(genJavaFolder);

		return GeneralUtils.concat(openJmlConfig, javaFiles);
	}
	
	public String[] getTypeCheckArgs()
	{
		// Arguments to run the OpenJML type checker on a set of Java files.
		// A requirement is to have OpenJML installed in $OPENJML
		// java
		// -jar
		// $OPENJML/openjml.jar
		// -classpath
		// codegen-runtime.jar
		// -check
		// <javafiles>
		
		return new String[] { JavaToolsUtils.JAVA, JavaToolsUtils.JAR_ARG, openJml.getAbsolutePath(),
				IOpenJmlConsts.CP_ARG,
				"\"" + cgRuntime.getAbsolutePath() + File.pathSeparator + vdm2jmlRuntime.getAbsolutePath() + "\"",
				IOpenJmlConsts.TC };
	}

	@Override
	public void beforeRunningOpenJmlProcess()
	{
		clearCodeFolder();
		generateJavaJml();
	}

	@Override
	protected String getPropertyId()
	{
		// Never configure execution
		return null;
	}
}
