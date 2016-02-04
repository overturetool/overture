package org.overture.vdm2jml.tests.exec;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.modules.AModuleModules;
import org.overture.codegen.ir.IRSettings;
import org.overture.codegen.tests.exec.util.CheckerTestBase;
import org.overture.codegen.tests.exec.util.ProcessResult;
import org.overture.codegen.tests.exec.util.testhandlers.ExecutableSpecTestHandler;
import org.overture.codegen.tests.exec.util.testhandlers.TestHandler;
import org.overture.codegen.utils.GeneralUtils;
import org.overture.codegen.utils.GeneratedData;
import org.overture.codegen.vdm2java.JavaCodeGen;
import org.overture.codegen.vdm2java.JavaSettings;
import org.overture.codegen.vdm2jml.JmlGenerator;
import org.overture.typechecker.util.TypeCheckerUtil;
import org.overture.typechecker.util.TypeCheckerUtil.TypeCheckResult;
import org.overture.vdm2jml.tests.OpenJmlValidationBase;
import org.overture.vdm2jml.tests.util.IOpenJmlConsts;

public abstract class JmlSlJavaGenTestBase extends CheckerTestBase
{
	public JmlSlJavaGenTestBase(File vdmSpec, TestHandler testHandler)
	{
		super(vdmSpec, testHandler);
	}
	
	public  JavaCodeGen getJavaGen()
	{
		JavaCodeGen javaCg = new JavaCodeGen();
		javaCg.setJavaSettings(getJavaSettings());
		javaCg.setSettings(getIrSettings());
		
		return javaCg;
	}
	
	public JavaSettings getJavaSettings()
	{
		JavaSettings javaSettings = new JavaSettings();
		javaSettings.setDisableCloning(false);
		javaSettings.setMakeClassesSerializable(true);
		javaSettings.setFormatCode(false);

		return javaSettings;
	}
	
	@Override
	public void genSourcesAndCompile()
	{
		genJavaSources(file);
		compile(consCpFiles());
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
			ProcessResult res = JmlExecTestBase.runProcess(args);
			Assert.assertTrue("Got errors when running process:\n" + res.getOutput(), res.getExitCode() == 0);
			
		} catch (IOException | InterruptedException e)
		{
			e.printStackTrace();
			Assert.fail("Could not type check model with OpenJML: " + e.getMessage());
		}
	}

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

		if (data == null)
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

}