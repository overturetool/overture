package org.overture.vdm2jml.tests.exec;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.lex.Dialect;
import org.overture.ast.modules.AModuleModules;
import org.overture.codegen.ir.IRSettings;
import org.overture.codegen.tests.exec.util.CommonJavaGenCheckerTest;
import org.overture.codegen.tests.exec.util.testhandlers.ExecutableSpecTestHandler;
import org.overture.codegen.tests.exec.util.testhandlers.TestHandler;
import org.overture.codegen.utils.GeneralUtils;
import org.overture.codegen.utils.GeneratedData;
import org.overture.codegen.vdm2jml.JmlGenerator;
import org.overture.config.Release;
import org.overture.typechecker.util.TypeCheckerUtil;
import org.overture.typechecker.util.TypeCheckerUtil.TypeCheckResult;
import org.overture.vdm2jml.tests.JmlSlTraceOutputTest;
import org.overture.vdm2jml.tests.OpenJmlValidationBase;
import org.overture.vdm2jml.tests.util.IOpenJmlConsts;

@RunWith(value = Parameterized.class)
public class JmlSlTraceJavaGenTest extends CommonJavaGenCheckerTest
{
	/* OpenJML crashes on these tests although the tests are correct.. */
	private static final List<String> SKIPPED = Arrays.asList("StateOtherModule.vdmsl");
	
	public JmlSlTraceJavaGenTest(String name, File vdmSpec,
			TestHandler testHandler)
	{
		super(vdmSpec, testHandler);
	}
	
	@Parameters(name = "{0}")
	public static Collection<Object[]> getData()
	{
		return collectTests(new File(JmlSlTraceOutputTest.ROOT),new JmlTraceTestHandler(Release.VDM_10, Dialect.VDM_SL));
	}
	
	@Override
	public void compile(File[] cpJars)
	{
		String openJmlDir = System.getenv(JmlExecTestBase.OPENJML_ENV_VAR);

		File cgRuntime = new File(JmlExecTestBase.CODEGEN_RUNTIME);
		
		File openJml = new File(openJmlDir, JmlExecTestBase.OPEN_JML);
		JmlExecTestBase.assumeFile(JmlExecTestBase.OPEN_JML, openJml);
		
		File vdm2jmlRuntime = new File(JmlExecTestBase.VDM_TO_JML_RUNTIME);
		JmlExecTestBase.assumeFile(JmlExecTestBase.VDM_TO_JML_RUNTIME, vdm2jmlRuntime);
		
		String[] args = GeneralUtils.concat(JmlExecTestBase.getTypeCheckArgs(outputDir, cgRuntime, vdm2jmlRuntime, openJml), new String[] {
				IOpenJmlConsts.RAC_TO_ASSERT_ARG });

		try
		{
			JmlExecTestBase.runProcess(args);
		} catch (IOException | InterruptedException e)
		{
			e.printStackTrace();
			Assert.fail("Could not type check model with OpenJML: " + e.getMessage());
		}
	}
	
	@Override
	public void genJavaSources(File vdmSource)
	{
		List<File> files = new LinkedList<File>();
		files.add(vdmSource);

		TypeCheckResult<List<AModuleModules>> tcResult = checkTcResult(TypeCheckerUtil.typeCheckSl(files));
		
		JmlGenerator jmlGen = new JmlGenerator(getJavaGen());
		GeneratedData data = null;
		try
		{
			data = jmlGen.generateJml(tcResult.result);
		} catch (AnalysisException e1)
		{
			Assert.fail("Could not type check VDM-SL model: " + e1.getMessage());
			e1.printStackTrace();
		}
		
		if(data == null)
		{
			Assert.fail("No data was data was generated from the VDM-SL model!");
		}

		jmlGen.getJavaGen().genJavaSourceFiles(outputDir, data.getClasses());
		
		
		if (testHandler instanceof ExecutableSpecTestHandler)
		{
			ExecutableSpecTestHandler ex = (ExecutableSpecTestHandler) testHandler;
			
			try
			{
				ex.writeMainClass(outputDir, jmlGen.getJavaSettings().getJavaRootPackage());
			} catch (IOException e)
			{
				Assert.fail("Got unexpected exception when attempting to generate Java/JML code: " + e.getMessage());
				e.printStackTrace();
			}
		}
	}
	
	public void assumeTest()
	{
		Assume.assumeFalse("OpenJML crashes on this test although it is correct", SKIPPED.contains(file.getName()));
	};
	
	@Override
	public IRSettings getIrSettings()
	{
		IRSettings irSettings = super.getIrSettings();
		irSettings.setGenerateTraces(true);
		irSettings.setGeneratePostConds(true);
		irSettings.setGeneratePreConds(true);
		
		return irSettings;
	}
	
	@Override
	public String getExecProperty()
	{
		return OpenJmlValidationBase.EXEC_PROPERTY;
	}
	
	@Override
	public String getTestsPropertyPrefix()
	{
		return "tests.vdm2jml.override.";
	}
	
	@Override
	protected String getPropertyId()
	{
		return "traces";
	}

}
