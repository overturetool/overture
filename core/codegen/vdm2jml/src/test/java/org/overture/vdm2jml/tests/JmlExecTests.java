package org.overture.vdm2jml.tests;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.overture.codegen.utils.GeneralUtils;
import org.overture.codegen.vdm2java.IJavaCodeGenConstants;
import org.overture.codegen.vdm2java.JavaCodeGenUtil;
import org.overture.codegen.vdm2java.JavaToolsUtils;
import org.overture.codegen.vdm2jml.IOpenJmlConsts;

@RunWith(Parameterized.class)
public class JmlExecTests extends OpenJmlValidationBase
{
	private static final String MAIN_CLASS_NAME = "Main";

	private static final String MAIN_CLASS_RES = "exec_entry_point";

	public static final String RESULT_FILE_EXT = ".result";
	
	private boolean isTypeChecked;
	protected File resultFile;

	public JmlExecTests(File inputFile, File resultFile)
	{
		super(inputFile);
		this.isTypeChecked = false;
		this.resultFile = resultFile;
	}
	
	@Before
	public void assumeTools()
	{
		assumeOpenJml();
		assumeJmlRuntime();
	}

	@Parameters(name = "{index}: {0}")
	public static Collection<Object[]> data()
	{
		File folder = new File(AnnotationTestsBase.TEST_RES_DYNAMIC_ANALYSIS_ROOT);
		List<File> files = GeneralUtils.getFiles(folder);

		List<Object[]> testData = new LinkedList<Object[]>();

		for (File file : files)
		{
			if (file.getName().endsWith(OpenJmlValidationBase.VDMSL_FILE_EXT))
			{
				File resFile = new File(file.getAbsolutePath() + RESULT_FILE_EXT);
				testData.add(new Object[] { file, resFile });
			}
		}

		return testData;
	}

	@Test
	public void execJml()
	{
		compileJmlJava();
		execJmlJava();
	}

	private void createExecEntryPoint()
	{
		File mainClassRes = new File(AnnotationTestsBase.TEST_RESOURCES_ROOT, MAIN_CLASS_RES);
		File mainClassJavaFile = new File(genJavaFolder, MAIN_CLASS_NAME + IJavaCodeGenConstants.JAVA_FILE_EXTENSION);

		try
		{
			// Create a Main.java (a class with a main method)
			FileUtils.copyFile(mainClassRes, mainClassJavaFile);
		} catch (IOException e)
		{
			Assume.assumeTrue("Problems generating execution entry point", false);
		}
	}
	
	public void compileJmlJava()
	{
		runOpenJmlProcess();
		isTypeChecked = true;
	}

	public void execJmlJava()
	{
		runOpenJmlProcess();
	}

	public String[] getTypeCheckArgs(File genJavaFolder)
	{
		// Compiles files with runtime assertions in preparation to execution
		// of the JML annotated Java code
		// java
		// -jar
		// $OPENJML/openjml.jar
		// -classpath
		// codegen-runtime.jar
		// -rac
		// -racCompileToJavaAssert (removed)
		// -no-purityCheck
		// <javafiles>
		
		String[] openJmlConfig = new String[] { JavaToolsUtils.JAVA, JavaToolsUtils.JAR_ARG,
				openJml.getAbsolutePath(), IOpenJmlConsts.CP_ARG,
				cgRuntime.getAbsolutePath(),
				IOpenJmlConsts.RAC_ARG,
				//IOpenJmlConsts.RAC_TO_ASSERT_ARG,
				IOpenJmlConsts.NO_PURITY_CHECKS_ARG };
		
		String[] javaFiles = JavaCodeGenUtil.findJavaFilePathsRec(genJavaFolder);

		return GeneralUtils.concat(openJmlConfig, javaFiles);
	}
	
	private String[] getExecArgs()
	{
		// Executes the OpenJML runtime assertion checker
		//java
		//-classpath
		//".:codegen-runtime.jar:jmlruntime.jar"
		//-ea
		//Main
		
		// Note that use of File.pathSeparatorChar makes it a platform dependent construction
		// of the classpath
		String runtimes = jmlRuntime.getAbsolutePath() + File.pathSeparatorChar
				+ openJml.getAbsolutePath() + File.pathSeparatorChar + genJavaFolder.getAbsolutePath();

		String[] args = new String[] { JavaToolsUtils.JAVA,
				JavaToolsUtils.CP_ARG, runtimes,
				JavaToolsUtils.ENABLE_ASSERTIONS_ARG, MAIN_CLASS_NAME };
		
		return args;
	}

	@Override
	public void beforeRunningOpenJmlProcess()
	{
		if(!isTypeChecked)
		{
			super.beforeRunningOpenJmlProcess();
			createExecEntryPoint();
		}
	}
	
	@Override
	public String[] getProcessArgs()
	{
		if(!isTypeChecked)
		{
			return getTypeCheckArgs(genJavaFolder);
		}
		else
		{
			return getExecArgs();
		}
	}
}
