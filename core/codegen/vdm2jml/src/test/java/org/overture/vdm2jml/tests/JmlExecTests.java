package org.overture.vdm2jml.tests;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.overture.ast.lex.LexLocation;
import org.overture.codegen.utils.GeneralUtils;
import org.overture.codegen.vdm2java.IJavaCodeGenConstants;
import org.overture.codegen.vdm2java.JavaCodeGenUtil;
import org.overture.codegen.vdm2java.JavaToolsUtils;
import org.overture.codegen.vdm2jml.IOpenJmlConsts;
import org.overture.test.framework.Properties;
import org.overture.vdm2jml.tests.util.ProcessResult;

@RunWith(Parameterized.class)
public class JmlExecTests extends OpenJmlValidationBase
{
	private static final String PROPERTY_ID = "exec";

	private static final String TESTS_VDM2JML_PROPERTY_PREFIX = "tests.vdm2jml.override.";

	private static final String MAIN_CLASS_NAME = "Main";

	private static final String MAIN_CLASS_RES = "exec_entry_point";

	public static final String RESULT_FILE_EXT = ".result";

	private boolean isTypeChecked;

	public JmlExecTests(File inputFile)
	{
		super(inputFile);
		this.isTypeChecked = false;
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

		return collectVdmslFiles(files);
	}

	@Test
	public void execJml()
	{
		try
		{
			configureResultGeneration();

			compileJmlJava();

			String actualRes = execJmlJava().toString().trim();

			if (Properties.recordTestResults)
			{
				storeResult(actualRes.toString());
			} else
			{
				try
				{
					String expectedRes = GeneralUtils.readFromFile(getResultFile()).trim();
					Assert.assertEquals("Expected result and actual result are different", expectedRes, actualRes);

				} catch (IOException e)
				{
					e.printStackTrace();
					Assert.assertTrue("Could not read the expected result from the result file: "
							+ e.getMessage(), false);
				}
			}
		} finally
		{
			unconfigureResultGeneration();
		}
	}

	public void storeResult(String resultStr)
	{
		File resultFile = getResultFile();

		PrintWriter printWriter = null;
		try
		{
			FileOutputStream fileOutStream = new FileOutputStream(resultFile, false);
			OutputStreamWriter outStream = new OutputStreamWriter(fileOutStream, "UTF-8");
			
			printWriter = new PrintWriter(outStream);
			printWriter.write(resultStr);
			printWriter.flush();
			
		} catch (UnsupportedEncodingException | FileNotFoundException e)
		{
			e.printStackTrace();
			Assert.assertTrue("Could not store result: " + e.getMessage(), false);
		} finally
		{
			if (printWriter != null)
			{
				printWriter.close();
			}
		}
	}

	private void createExecEntryPoint()
	{
		File mainClassRes = new File(AnnotationTestsBase.TEST_RESOURCES_ROOT, MAIN_CLASS_RES);
		File mainClassJavaFile = new File(genJavaFolder, MAIN_CLASS_NAME
				+ IJavaCodeGenConstants.JAVA_FILE_EXTENSION);

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
		ProcessResult processResult = runOpenJmlProcess();

		assertNoProcessErrors(processResult);

		isTypeChecked = true;
	}

	public StringBuilder execJmlJava()
	{
		ProcessResult processResult = runOpenJmlProcess();

		assertNoProcessErrors(processResult);

		return processResult.getOutput();
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
		// -racCompileToJavaAssert
		// -no-purityCheck
		// <javafiles>

		String[] openJmlConfig = new String[] { JavaToolsUtils.JAVA,
				JavaToolsUtils.JAR_ARG, openJml.getAbsolutePath(),
				IOpenJmlConsts.CP_ARG, cgRuntime.getAbsolutePath(),
				IOpenJmlConsts.RAC_ARG,
				IOpenJmlConsts.RAC_TO_ASSERT_ARG,
				IOpenJmlConsts.NO_PURITY_CHECKS_ARG };

		String[] javaFiles = JavaCodeGenUtil.findJavaFilePathsRec(genJavaFolder);

		return GeneralUtils.concat(openJmlConfig, javaFiles);
	}

	private String[] getExecArgs()
	{
		// Executes the OpenJML runtime assertion checker
		// java
		// -classpath
		// ".:codegen-runtime.jar:jmlruntime.jar"
		// -ea
		// Main

		// Note that use of File.pathSeparatorChar makes it a platform dependent construction
		// of the classpath
		String runtimes = jmlRuntime.getAbsolutePath() + File.pathSeparatorChar
				+ openJml.getAbsolutePath() + File.pathSeparatorChar
				+ genJavaFolder.getAbsolutePath() + File.pathSeparatorChar
				+ cgRuntime.getAbsolutePath();
		
		String[] args = new String[] { JavaToolsUtils.JAVA,
				JavaToolsUtils.CP_ARG, runtimes,
				JavaToolsUtils.ENABLE_ASSERTIONS_ARG, MAIN_CLASS_NAME };

		return args;
	}

	protected File getResultFile()
	{
		File resultFile = new File(inputFile.getAbsolutePath()
				+ RESULT_FILE_EXT);

		if (!resultFile.exists())
		{
			resultFile.getParentFile().mkdirs();
			try
			{
				resultFile.createNewFile();
			} catch (IOException e)
			{
				Assume.assumeTrue("Problems creating result file: "
						+ e.getMessage(), false);
				e.printStackTrace();
				return null;
			}
		}

		return resultFile;
	}

	@Override
	public void beforeRunningOpenJmlProcess()
	{
		if (!isTypeChecked)
		{
			super.beforeRunningOpenJmlProcess();
			createExecEntryPoint();
		}
	}

	@Override
	public String[] getProcessArgs()
	{
		if (!isTypeChecked)
		{
			return getTypeCheckArgs(genJavaFolder);
		} else
		{
			return getExecArgs();
		}
	}

	protected String getPropertyId()
	{
		return PROPERTY_ID;
	}

	protected void configureResultGeneration()
	{
		LexLocation.absoluteToStringLocation = false;
		if (System.getProperty(TESTS_VDM2JML_PROPERTY_PREFIX + "all") != null
				|| getPropertyId() != null
				&& System.getProperty(TESTS_VDM2JML_PROPERTY_PREFIX
						+ getPropertyId()) != null)
		{
			Properties.recordTestResults = true;
		}
	}

	protected void unconfigureResultGeneration()
	{
		Properties.recordTestResults = false;
	}
}
