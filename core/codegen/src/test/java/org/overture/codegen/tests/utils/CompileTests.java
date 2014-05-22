package org.overture.codegen.tests.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.List;

import org.overture.codegen.tests.ClassicSpecTest;
import org.overture.codegen.tests.ComplexExpressionTest;
import org.overture.codegen.tests.ConfiguredStringGenerationTest;
import org.overture.codegen.tests.ExpressionTest;
import org.overture.codegen.tests.FunctionValueTest;
import org.overture.codegen.tests.SpecificationTest;
import org.overture.codegen.utils.GeneralCodeGenUtils;
import org.overture.codegen.utils.GeneralUtils;
import org.overture.config.Release;
import org.overture.interpreter.values.Value;

public class CompileTests
{
	private static final String CG_VALUE_BINARY_FILE = "target\\cgtest\\myData.bin";
	private static final String TEMP_DIR = "target\\cgtest";
	private static final String SRC_JAVA_LIB = "..\\codegen-runtime\\src\\main\\java\\org\\overture\\codegen\\runtime";
	private static final String TARGET_JAVA_LIB = "target\\cgtest\\org\\overture\\codegen\\runtime";
	
	private static final List<String> FOLDER_NAMES_TO_AVOID = Arrays.asList(new String[]{"runtime"});

	private static final String RESULT_FILE_EXTENSION = ".result";
	
	public static final boolean RUN_EXP_TESTS = true;
	public static final boolean RUN_COMPLEX_EXP_TESTS = true;
	public static final boolean RUN_EXECUTING_CLASSIC_SPEC_TESTS = true;
	public static final boolean RUN_NON_EXECUTING_VDM10_SPEC_TESTS = true;
	public static final boolean RUN_FUNCTION_VALUE_TESTS = true;
	public static final boolean RUN_CONFIGURED_STRING_GENERATION_TESTS = true;
	
	private List<File> testInputFiles;
	private List<File> resultFiles;
	
	public static void main(String[] args) throws IOException
	{
		new CompileTests().runTests();
	}

	private void runTests() throws IOException
	{
		addPath(new File(TEMP_DIR));
		
		long startTimeMs = System.currentTimeMillis();
		
		File srcJavaLib = new File(SRC_JAVA_LIB);
		File utils = new File(TARGET_JAVA_LIB);
		
		GeneralCodeGenUtils.copyDirectory(srcJavaLib, utils);

		if(RUN_EXP_TESTS)
		{
			runExpTests();
		}

		if (RUN_COMPLEX_EXP_TESTS)
		{
			runComplexExpTests();
		}

		if (RUN_EXECUTING_CLASSIC_SPEC_TESTS)
		{
			runExecutingClassicSpecTests();
		}

		if(RUN_NON_EXECUTING_VDM10_SPEC_TESTS)
		{
			runNonExecutingVdm10Tests();
		}
		
		if(RUN_FUNCTION_VALUE_TESTS)
		{
			runFunctionValueTests();
		}
		
		if(RUN_CONFIGURED_STRING_GENERATION_TESTS)
		{
			runConfiguredStringTests();
		}
		
		long endTimeMs = System.currentTimeMillis();
		
		long totalTimeMs = (endTimeMs - startTimeMs);
		
		long minutes = totalTimeMs / (60 * 1000);
		long seconds = (totalTimeMs % (60 * 1000)) / 1000;
		
		System.out.println("Time: " + String.format("%02d:%02d", minutes, seconds) + ".");
	}

	private void runConfiguredStringTests() throws IOException
	{
		System.out.println("Beginning configured strings..\n");

		testInputFiles = TestUtils.getTestInputFiles(new File(ConfiguredStringGenerationTest.ROOT));
		resultFiles = TestUtils.getFiles(new File(ConfiguredStringGenerationTest.ROOT), RESULT_FILE_EXTENSION);
		
		runTests(testInputFiles, resultFiles, new ExecutableSpecTestHandler(Release.VDM_10), false);
		
		System.out.println("\n********");
		System.out.println("Finished with configured strings");
		System.out.println("********\n");
	}

	private void runFunctionValueTests() throws IOException
	{
		System.out.println("Beginning function values..\n");

		testInputFiles = TestUtils.getTestInputFiles(new File(FunctionValueTest.ROOT));
		resultFiles = TestUtils.getFiles(new File(FunctionValueTest.ROOT), RESULT_FILE_EXTENSION);
		
		runTests(testInputFiles, resultFiles, new ExecutableSpecTestHandler(Release.VDM_10), false);
		
		System.out.println("\n********");
		System.out.println("Finished with function values");
		System.out.println("********\n");
	}

	private void runNonExecutingVdm10Tests() throws IOException
	{
		System.out.println("Beginning with specifications..");
		
		testInputFiles = TestUtils.getTestInputFiles(new File(SpecificationTest.ROOT));
		resultFiles = TestUtils.getFiles(new File(SpecificationTest.ROOT), RESULT_FILE_EXTENSION);
		
		runTests(testInputFiles, resultFiles, new NonExecutableSpecTestHandler(), false);
		
		System.out.println("\n********");
		System.out.println("Finished with specifications");
		System.out.println("********\n");
	}

	private void runExecutingClassicSpecTests() throws IOException
	{
		System.out.println("Beginning classic specifications..\n");

		testInputFiles = TestUtils.getTestInputFiles(new File(ClassicSpecTest.ROOT));
		resultFiles = TestUtils.getFiles(new File(ClassicSpecTest.ROOT), RESULT_FILE_EXTENSION);
		
		runTests(testInputFiles, resultFiles, new ExecutableSpecTestHandler(Release.CLASSIC), false);
		
		System.out.println("\n********");
		System.out.println("Finished with classic specifications");
		System.out.println("********\n");
	}

	private void runComplexExpTests() throws IOException
	{
		System.out.println("Beginning complex expressions..\n");

		testInputFiles = TestUtils.getTestInputFiles(new File(ComplexExpressionTest.ROOT));
		resultFiles = TestUtils.getFiles(new File(ComplexExpressionTest.ROOT), RESULT_FILE_EXTENSION);
		
		runTests(testInputFiles, resultFiles, new ExecutableSpecTestHandler(Release.VDM_10), false);
		
		System.out.println("\n********");
		System.out.println("Finished with complex expressions");
		System.out.println("********\n");
	}

	private void runExpTests() throws IOException
	{
		System.out.println("Beginning expressions..\n");

		testInputFiles = TestUtils.getTestInputFiles(new File(ExpressionTest.ROOT));
		resultFiles = TestUtils.getFiles(new File(ExpressionTest.ROOT), RESULT_FILE_EXTENSION);
		
		runTests(testInputFiles, resultFiles, new ExpressionTestHandler(Release.VDM_10), true);
		
		System.out.println("\n********");
		System.out.println("Finished with expressions");
		System.out.println("********\n");
	}
	
	private void addPath(File f)
	{
		try
		{
			URL u = f.toURI().toURL();
			URLClassLoader urlClassLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
			Class<URLClassLoader> urlClass = URLClassLoader.class;
			Method method = urlClass.getDeclaredMethod("addURL", new Class[] { URL.class });
			method.setAccessible(true);
			method.invoke(urlClassLoader, new Object[] { u });
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void runTests(List<File> testInputFiles, List<File> resultFiles, TestHandler testHandler, boolean printInput) throws IOException
	{
		if(testInputFiles.size() != resultFiles.size())
			throw new IllegalArgumentException("Number of test input files and number of result files differ");
		
		int testCount = testInputFiles.size();
		
		for(int i = 0; i < testCount; i++)
		{
			String resultFileName = resultFiles.get(i).getName();
			String inputFileName = testInputFiles.get(i).getName();
			
			if(!resultFileName.equals(inputFileName + RESULT_FILE_EXTENSION))
				throw new IllegalArgumentException("Test input file and result file do not match. " + "Test input file: " + inputFileName + ". Result file: " + resultFileName);
		}
				
		File parent = new File(TEMP_DIR);
		parent.mkdirs();
		
		for(int i = 0; i < testCount; i++)
		{
			File currentInputFile = testInputFiles.get(i);
			
			GeneralUtils.deleteFolderContents(parent, FOLDER_NAMES_TO_AVOID);
			
			//Calculating the Java result:
			File file = resultFiles.get(i);
			
			testHandler.writeGeneratedCode(parent, file);
			
			boolean compileOk = JavaCommandLineCompiler.compile(parent, null);
			System.out.println("Test:" + (1 + i)  + ". Name: " + file.getName() + " " + (compileOk ? "Compile OK" : "ERROR"));

			if(!compileOk)
				continue;
			
			if (testHandler instanceof ExecutableTestHandler)
			{
				ExecutableTestHandler executableTestHandler = (ExecutableTestHandler) testHandler;
			
				// Calculating the VDM Result:
				Value vdmResult;

				try
				{
					vdmResult = executableTestHandler.interpretVdm(currentInputFile);
				} catch (Exception e1)
				{
					e1.printStackTrace();
					return;
				}

				String javaResult = JavaExecution.run(parent, TestHandler.MAIN_CLASS);

				File dataFile = new File(CG_VALUE_BINARY_FILE);
				FileInputStream fin = new FileInputStream(dataFile);
				ObjectInputStream ois = new ObjectInputStream(fin);

				Object cgValue = null;
				try
				{
					cgValue = (Object) ois.readObject();
				} catch (ClassNotFoundException e)
				{
					e.printStackTrace();
					return;
				} finally
				{
					ois.close();
				}

				// Comparison of VDM and Java results
				ComparisonCG comp = new ComparisonCG(currentInputFile);
				boolean equal = comp.compare(cgValue, vdmResult);

				if (printInput)
				{
					String vdmInput = GeneralUtils.readFromFile(currentInputFile);
					System.out.println("VDM:  " + vdmInput);

					String generatedCode = GeneralUtils.readFromFile(file).replace('#', ' ');
					System.out.println("Java: " + generatedCode);
				} else
				{
					System.out.println("CG Test: " + currentInputFile.getName());
				}

				System.out.println("VDM ~>  " + vdmResult);
				System.out.print("Java ~> " + javaResult);

				if (equal)
				{
					System.out.println("Evaluation OK: VDM value and Java value are equal\n");
				} else
				{
					System.err.println("ERROR: VDM value and Java value are different");
				}
			}
		}
	}
}
