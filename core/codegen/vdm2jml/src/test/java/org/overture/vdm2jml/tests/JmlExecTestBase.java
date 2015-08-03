package org.overture.vdm2jml.tests;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.overture.ast.lex.LexLocation;
import org.overture.codegen.utils.GeneralUtils;
import org.overture.codegen.vdm2java.IJavaConstants;
import org.overture.codegen.vdm2java.JavaCodeGen;
import org.overture.codegen.vdm2java.JavaCodeGenUtil;
import org.overture.codegen.vdm2java.JavaToolsUtils;
import org.overture.codegen.vdm2jml.IOpenJmlConsts;
import org.overture.test.framework.Properties;
import org.overture.vdm2jml.tests.util.ProcessResult;

public abstract class JmlExecTestBase extends OpenJmlValidationBase
{
	public static final String TEST_RES_DYNAMIC_ANALYSIS_ROOT = AnnotationTestsBase.TEST_RESOURCES_ROOT
			+ "dynamic_analysis" + File.separatorChar;
	
	public static final String TESTS_VDM2JML_PROPERTY_PREFIX = "tests.vdm2jml.override.";
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
		assumeOpenJml();
		assumeJmlRuntime();
	}

	@Test
	public void execJml()
	{
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
					checkGenJavaJml();
	
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

	protected void checkOpenJmlOutput(String actualRes) throws IOException
	{
		String expectedRes = GeneralUtils.readFromFile(getResultFile()).trim();
		Assert.assertEquals("Expected result and actual result are different", expectedRes, actualRes);
	}

	protected void checkGenJavaJml()
	{
		List<File> storedJavaJmlFiles = collectStoredJavaJmlFiles();
		List<File> genJavaJmlFiles = collectGenJavaJmlFiles();
		
		Assert.assertTrue("The number of stored Java/JML files differs "
				+ "from the number of generated Java/JML files", storedJavaJmlFiles.size() == genJavaJmlFiles.size());
	
		Comparator<File> comp = new Comparator<File>() {
			
	        @Override
	        public int compare(File f1, File f2) {
	        	
	        	// Reverse paths because root folders differ
	        	String f1PathRev = new StringBuilder(f1.getAbsolutePath()).reverse().toString();
	        	String f2PathRev = new StringBuilder(f2.getAbsolutePath()).reverse().toString();
	        	
	        	return f1PathRev.compareTo(f2PathRev);
	        }
	    };
	    
	    Collections.sort(storedJavaJmlFiles, comp);
	    Collections.sort(genJavaJmlFiles, comp);
	    
	    for(int i = 0; i < storedJavaJmlFiles.size(); i++)
	    {
	    	try
			{
				String storedContent = GeneralUtils.readFromFile(storedJavaJmlFiles.get(i)).trim();
				String genContent = GeneralUtils.readFromFile(genJavaJmlFiles.get(i)).trim();
	
				Assert.assertEquals("Stored Java/JML is different from generared Java/JML", storedContent, genContent);
			} catch (IOException e)
			{
				Assert.fail("Problems comparing stored and generated Java/JML");
				e.printStackTrace();
			}
	    }
	}

	protected void storeResult(String resultStr)
	{
		storeJmlOutput(resultStr);
		storeGeneratedJml();
	}

	protected File getTestDataFolder()
	{
		return inputFile.getParentFile();
	}

	protected void storeGeneratedJml()
	{
		for(File file : collectStoredJavaJmlFiles())
		{
			Assert.assertTrue("Problems deleting stored Java/JML file", file.exists() && file.delete());
		}
		
		try
		{
			List<File> filesToStore = collectGenJavaJmlFiles();
	
			File testFolder = getTestDataFolder();
	
			for (File file : filesToStore)
			{
				File javaFile = new File(testFolder, file.getName());
				FileUtils.copyFile(file, javaFile);
			}
		} catch (IOException e)
		{
			e.printStackTrace();
			Assert.assertTrue("Problems storing generated JML: "
					+ e.getMessage(), false);
		}
	}

	protected List<File> collectStoredJavaJmlFiles()
	{
		List<File> files = GeneralUtils.getFiles(getTestDataFolder());
		
		LinkedList<File> javaFiles = new LinkedList<File>();
		
		for(File f : files)
		{
			if(f.getName().endsWith(IJavaConstants.JAVA_FILE_EXTENSION))
			{
				javaFiles.add(f);
			}
		}
		
		return javaFiles;
	}

	protected List<File> collectGenJavaJmlFiles()
	{
		List<File> files = GeneralUtils.getFilesRecursively(genJavaFolder);
		
		String projDir = File.separatorChar + DEFAULT_JAVA_ROOT_PACKAGE
				+ File.separatorChar;
		String quotesDir = File.separatorChar + JavaCodeGen.JAVA_QUOTES_PACKAGE
				+ File.separatorChar;
		
		List<File> filesToStore = new LinkedList<File>();
	
		for (File file : files)
		{
			String absPath = file.getAbsolutePath();
	
			if (absPath.endsWith(IJavaConstants.JAVA_FILE_EXTENSION)
					&& absPath.contains(projDir)
					&& !absPath.contains(quotesDir))
			{
				filesToStore.add(file);
			}
		}
		
		return filesToStore;
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
	
		String[] openJmlConfig = new String[] { JavaToolsUtils.JAVA,
				JavaToolsUtils.JAR_ARG, openJml.getAbsolutePath(),
				IOpenJmlConsts.CP_ARG, cgRuntime.getAbsolutePath(),
				IOpenJmlConsts.RAC_ARG,
				/*IOpenJmlConsts.RAC_TO_ASSERT_ARG,*/
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
				+ cgRuntime.getAbsolutePath();
		
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
			codeGenerateInputFile();
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

	abstract protected String getPropertyId();

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