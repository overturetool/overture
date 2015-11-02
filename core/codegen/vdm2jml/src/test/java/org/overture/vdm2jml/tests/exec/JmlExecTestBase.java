package org.overture.vdm2jml.tests.exec;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.overture.codegen.utils.GeneralUtils;
import org.overture.codegen.vdm2java.IJavaConstants;
import org.overture.codegen.vdm2java.JavaCodeGenUtil;
import org.overture.codegen.vdm2java.JavaToolsUtils;
import org.overture.test.framework.Properties;
import org.overture.vdm2jml.tests.AnnotationTestsBase;
import org.overture.vdm2jml.tests.OpenJmlValidationBase;
import org.overture.vdm2jml.tests.util.IOpenJmlConsts;
import org.overture.vdm2jml.tests.util.ProcessResult;

public abstract class JmlExecTestBase extends OpenJmlValidationBase
{
	public static final String TEST_RES_DYNAMIC_ANALYSIS_ROOT = AnnotationTestsBase.TEST_RESOURCES_ROOT
			+ "dynamic_analysis" + File.separatorChar;
	
	public static final String MAIN_CLASS_NAME = "Main";
	public static final String MAIN_CLASS_RES = "exec_entry_point";
	public static final String RESULT_FILE_EXT = ".result";
	public static final String DEFAULT_JAVA_ROOT_PACKAGE = "project";

	protected boolean isTypeChecked;

	public JmlExecTestBase(File inputFile)
	{
		super(inputFile);
		this.isTypeChecked = false;
	}

	@Before
	public void assumeTools()
	{
		Assume.assumeTrue(String.format("Execution test will only run if the "
				+ "property '%s' is passed", EXEC_PROPERTY), System.getProperty(EXEC_PROPERTY) != null);
		
		assumeOpenJml();
		assumeJmlRuntime();
	}

	@Test
	public void execJml()
	{
		checkIfSkipped();
		try
		{
			configureResultGeneration();
	
			compileJmlJava();
			
			String actualRes = processResultStr(execJmlJava().toString());
	
			if (Properties.recordTestResults)
			{
				storeResult(actualRes.toString());
			} else
			{
				try
				{
					checkOpenJmlOutput(actualRes);
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

	private void checkIfSkipped()
	{
		Assume.assumeFalse("OpenJML cannot compile this test - there is a bug", inputFile.getName().equals("Exists1.vdmsl"));
	}

	protected void checkOpenJmlOutput(String actualRes) throws IOException
	{
		String expectedRes = GeneralUtils.readFromFile(getResultFile()).trim();
		Assert.assertEquals("Expected result and actual result are different", expectedRes, actualRes);
	}

	protected void storeResult(String resultStr)
	{
		storeJmlOutput(resultStr);
	}

	protected void storeJmlOutput(String resultStr)
	{
		resultStr = processResultStr(resultStr);
		
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

	protected String processResultStr(String resultStr)
	{
		resultStr = resultStr.trim();
		resultStr = resultStr.replaceAll("\r", "");
	
		String[] lines = resultStr.split("\n");
		StringBuilder sb = new StringBuilder();
		Pattern pattern = Pattern.compile("(?m)^.*?[^a-zA-Z]([a-zA-Z]+\\.java:[0-9]+:.*?)(:|$)");
		
		for(String line : lines)
		{
			Matcher matcher = pattern.matcher(line);
			
			if(matcher.find())
			{
				sb.append(matcher.group(1));
			}
			else
			{
				sb.append(line);
			}
			sb.append('\n');
		}
		
		return sb.toString().trim();
	}

	protected void createExecEntryPoint()
	{
		File mainClassRes = new File(AnnotationTestsBase.TEST_RESOURCES_ROOT, MAIN_CLASS_RES);
		File mainClassJavaFile = new File(genJavaFolder, MAIN_CLASS_NAME
				+ IJavaConstants.JAVA_FILE_EXTENSION);
	
		try
		{
			// Create a Main.java (a class with a main method)
			FileUtils.copyFile(mainClassRes, mainClassJavaFile);
		} catch (IOException e)
		{
			Assume.assumeTrue("Problems generating execution entry point", false);
		}
	}

	protected void compileJmlJava()
	{
		ProcessResult processResult = runOpenJmlProcess();
	
		assertNoProcessErrors(processResult);
	
		isTypeChecked = true;
	}

	protected StringBuilder execJmlJava()
	{
		ProcessResult processResult = runOpenJmlProcess();
	
		assertNoProcessErrors(processResult);
	
		return processResult.getOutput();
	}

	protected String[] getTypeCheckArgs(File genJavaFolder)
	{
		// Compiles files with runtime assertions in preparation to execution
		// of the JML annotated Java code
		// java
		// -jar
		// $OPENJML/openjml.jar
		// -classpath
		// codegen-runtime.jar
		// -rac
		// -racCompileToJavaAssert (currently disabled)
		// -no-purityCheck
		// <javafiles>
	
		String[] openJmlConfig = new String[] { JavaToolsUtils.JAVA, JavaToolsUtils.JAR_ARG, openJml.getAbsolutePath(),
				IOpenJmlConsts.CP_ARG,
				"\"" + cgRuntime.getAbsolutePath() + File.pathSeparator + vdm2jmlRuntime.getAbsolutePath() + "\"",
				IOpenJmlConsts.RAC_ARG,
				/* IOpenJmlConsts.RAC_TO_ASSERT_ARG, */
				IOpenJmlConsts.NO_PURITY_CHECKS_ARG };
	
		String[] javaFiles = JavaCodeGenUtil.findJavaFilePathsRec(genJavaFolder);
	
		return GeneralUtils.concat(openJmlConfig, javaFiles);
	}

	protected String[] getExecArgs()
	{
		// Executes the OpenJML runtime assertion checker
		// java
		// -classpath
		// ".:codegen-runtime.jar:jmlruntime.jar"
		// -ea (currently disabled)
		// Main
	
		// Note that use of File.pathSeparatorChar makes it a platform dependent construction
		// of the classpath
		String runtimes = jmlRuntime.getAbsolutePath() + File.pathSeparatorChar
				+ openJml.getAbsolutePath() + File.pathSeparatorChar
				+ genJavaFolder.getAbsolutePath() + File.pathSeparatorChar
				+ cgRuntime.getAbsolutePath() + File.pathSeparatorChar
				+ vdm2jmlRuntime.getAbsolutePath();
		
		String[] args = new String[] { JavaToolsUtils.JAVA,
				JavaToolsUtils.CP_ARG, runtimes,
				/*JavaToolsUtils.ENABLE_ASSERTIONS_ARG,*/ MAIN_CLASS_NAME };
	
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
				Assert.assertTrue("Problems creating result file: "
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
			clearCodeFolder();
			createExecEntryPoint();
			generateJavaJml();
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
}