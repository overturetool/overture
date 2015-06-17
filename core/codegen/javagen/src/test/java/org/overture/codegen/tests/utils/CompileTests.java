/*
 * #%~
 * VDM Code Generator
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
package org.overture.codegen.tests.utils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.List;

import org.overture.ast.lex.Dialect;
import org.overture.codegen.tests.BindTest;
import org.overture.codegen.tests.ClassicSpecTest;
import org.overture.codegen.tests.ComplexExpressionTest;
import org.overture.codegen.tests.ConcurrencyClassicSpecTests;
import org.overture.codegen.tests.ConcurrencyTests;
import org.overture.codegen.tests.ConfiguredCloningTest;
import org.overture.codegen.tests.ConfiguredStringGenerationTest;
import org.overture.codegen.tests.ExpressionTest;
import org.overture.codegen.tests.FunctionValueTest;
import org.overture.codegen.tests.NameNormalising;
import org.overture.codegen.tests.PackageTest;
import org.overture.codegen.tests.PatternTest;
import org.overture.codegen.tests.PrePostTest;
import org.overture.codegen.tests.RtTest;
import org.overture.codegen.tests.SlTest;
import org.overture.codegen.tests.SpecificationTest;
import org.overture.codegen.tests.TracesExpansionTest;
import org.overture.codegen.tests.TracesVerdictTest;
import org.overture.codegen.tests.UnionTypeTest;
import org.overture.codegen.utils.GeneralCodeGenUtils;
import org.overture.codegen.utils.GeneralUtils;
import org.overture.config.Release;
import org.overture.interpreter.runtime.ContextException;

public class CompileTests
{
	private static final String TEMP_DIR = "target" + File.separatorChar
			+ "cgtest";
	private static final String SRC_JAVA_LIB = ".." + File.separatorChar
			+ "codegen-runtime" + File.separatorChar + "src"
			+ File.separatorChar + "main" + File.separatorChar + "java"
			+ File.separatorChar + "org" + File.separatorChar + "overture"
			+ File.separatorChar + "codegen" + File.separatorChar + "runtime";
	private static final String TARGET_JAVA_LIB = "target" + File.separatorChar
			+ "cgtest" + File.separatorChar + "org" + File.separatorChar
			+ "overture" + File.separatorChar + "codegen" + File.separatorChar
			+ "runtime";

	private static final List<String> FOLDER_NAMES_TO_AVOID = Arrays.asList(new String[] { "runtime" });

	public static final String RESULT_FILE_EXTENSION = ".result";

	public static final boolean RUN_EXP_TESTS = true;
	public static final boolean RUN_SL_TESTS = true;
	public static final boolean RUN_COMPLEX_EXP_TESTS = true;
	public static final boolean RUN_NON_EXECUTING_VDM10_SPEC_TESTS = true;
	public static final boolean RUN_FUNCTION_VALUE_TESTS = true;
	public static final boolean RUN_CONFIGURED_STRING_GENERATION_TESTS = true;
	public static final boolean RUN_PATTERN_TESTS = true;
	public static final boolean RUN_UNION_TESTS = true;
	public static final boolean RUN_CONCURRENCY_TESTS = true;
	public static final boolean RUN_CONCURRENCY_CLASSIC_TESTS = true;
	public static final boolean RUN_RT_TESTS = true;
	public static final boolean RUN_PACKAGE_TESTS = true;
	public static final boolean RUN_NAMING_NORMALISING_TESTS = true;
	public static final boolean RUN_BIND_TESTS = true;
	public static final boolean PRE_POST_TESTS = true;
	public static final boolean RUN_EXECUTING_CLASSIC_SPEC_TESTS = true;
	public static final boolean RUN_CONFIGURED_CLONE_TESTS = true;
	public static final boolean RUN_TRACES_EXPANSION_TESTS = true;
	public static final boolean RUN_TRACES_VERDICT_TESTS = true;
	
	private List<File> testInputFiles;
	private List<File> resultFiles;

	private static int testNumber = 1;
	
	public static void main(String[] args) throws IOException
	{
		new CompileTests().runTests();
		
		//Force shutdown (interpreter threads might be hanging)
		System.exit(0);
	}

	private void runTests() throws IOException
	{
		addPath(new File(TEMP_DIR));

		long startTimeMs = System.currentTimeMillis();

		File srcJavaLib = new File(SRC_JAVA_LIB);
		File utils = new File(TARGET_JAVA_LIB);

		GeneralCodeGenUtils.copyDirectory(srcJavaLib, utils);

		testNumber = 1;
		
		if (RUN_EXP_TESTS)
		{
			runExpTests();
		}
		
		if(RUN_SL_TESTS)
		{
			runSlTests();
		}

		if (RUN_COMPLEX_EXP_TESTS)
		{
			runComplexExpTests();
		}
		
		if (RUN_NON_EXECUTING_VDM10_SPEC_TESTS)
		{
			runNonExecutingVdm10Tests();//not moved to unit test
		}
		
		if (RUN_FUNCTION_VALUE_TESTS)
		{
			runFunctionValueTests();
		}

		if (RUN_CONFIGURED_STRING_GENERATION_TESTS)
		{
			runConfiguredStringTests();
		}

		if (RUN_PATTERN_TESTS)
		{
			runPatternTests();
		}

		if (RUN_UNION_TESTS)
		{
			runUnionTests();
		}

		if (RUN_CONCURRENCY_TESTS)
		{
			runConcurrencyTests();
		}
		
		if (RUN_CONCURRENCY_CLASSIC_TESTS)
		{
			runConcurrencyClassicTests();
		}

		if(RUN_RT_TESTS)
		{
			runRtTests();
		}
		
		if(RUN_PACKAGE_TESTS)
		{
			runPackageTests();
		}
		
		if(RUN_NAMING_NORMALISING_TESTS)
		{
			runNamingNormalisingTests();
		}
		
		if (RUN_BIND_TESTS)
		{
			runBindTests();
		}
		
		if(PRE_POST_TESTS)
		{
			runPrePostTests();
		}
		
		if (RUN_EXECUTING_CLASSIC_SPEC_TESTS)
		{
			runExecutingClassicSpecTests();
		}
		
		if (RUN_CONFIGURED_CLONE_TESTS)
		{
			runConfiguredCloningTests();
		}
		
		if(RUN_TRACES_EXPANSION_TESTS)
		{
			runTraceExpansionTests();
		}

		if(RUN_TRACES_VERDICT_TESTS)
		{
			runTraceVerdictTests();
		}
		
		long endTimeMs = System.currentTimeMillis();

		long totalTimeMs = endTimeMs - startTimeMs;

		long minutes = totalTimeMs / (60 * 1000);
		long seconds = totalTimeMs % (60 * 1000) / 1000;

		System.out.println("Time: "
				+ String.format("%02d:%02d", minutes, seconds) + ".");
	}

	private void runSlTests() throws IOException
	{
		System.out.println("Beginning SL tests..\n");

		testInputFiles = TestUtils.getTestInputFiles(new File(SlTest.ROOT));
		resultFiles = TestUtils.getFiles(new File(SlTest.ROOT), RESULT_FILE_EXTENSION);

		runTests(testInputFiles, resultFiles, new ExecutableSpecTestHandler(Release.VDM_10, Dialect.VDM_SL), false);

		System.out.println("\n********");
		System.out.println("Finished with SL tests");
		System.out.println("********\n");		
	}

	private void runTraceVerdictTests() throws IOException
	{
		System.out.println("Beginning Trace verdict tests..\n");

		testInputFiles = TestUtils.getTestInputFiles(new File(TracesVerdictTest.ROOT));
		resultFiles = TestUtils.getFiles(new File(TracesVerdictTest.ROOT), RESULT_FILE_EXTENSION);

		runTests(testInputFiles, resultFiles, new TraceHandler(Release.VDM_10, Dialect.VDM_RT), false);

		System.out.println("\n********");
		System.out.println("Finished with Trace verdict tests");
		System.out.println("********\n");	
	}

	private void runTraceExpansionTests() throws IOException
	{
		System.out.println("Beginning Trace expansion tests..\n");

		testInputFiles = TestUtils.getTestInputFiles(new File(TracesExpansionTest.ROOT));
		resultFiles = TestUtils.getFiles(new File(TracesExpansionTest.ROOT), RESULT_FILE_EXTENSION);

		runTests(testInputFiles, resultFiles, new TraceHandler(Release.VDM_10, Dialect.VDM_RT), false);

		System.out.println("\n********");
		System.out.println("Finished with Trace expansion tests");
		System.out.println("********\n");
	}
	
	private void runNamingNormalisingTests() throws IOException
	{
		System.out.println("Beginning name normalising tests..\n");

		testInputFiles = TestUtils.getTestInputFiles(new File(NameNormalising.ROOT));
		resultFiles = TestUtils.getFiles(new File(NameNormalising.ROOT), RESULT_FILE_EXTENSION);

		runTests(testInputFiles, resultFiles, new ExecutableSpecTestHandler(Release.VDM_10, Dialect.VDM_RT), false);

		System.out.println("\n********");
		System.out.println("Finished with name normalising tests");
		System.out.println("********\n");
	}

	private void runPackageTests() throws IOException
	{
		System.out.println("Beginning package tests..\n");

		testInputFiles = TestUtils.getTestInputFiles(new File(PackageTest.ROOT));
		resultFiles = TestUtils.getFiles(new File(PackageTest.ROOT), RESULT_FILE_EXTENSION);

		runTests(testInputFiles, resultFiles, new ExecutableSpecTestHandler(Release.VDM_10, Dialect.VDM_RT), false, "my.model");

		System.out.println("\n********");
		System.out.println("Finished with package tests");
		System.out.println("********\n");
	}

	private void runRtTests() throws IOException
	{
		System.out.println("Beginning RT tests..\n");

		testInputFiles = TestUtils.getTestInputFiles(new File(RtTest.ROOT));
		resultFiles = TestUtils.getFiles(new File(RtTest.ROOT), RESULT_FILE_EXTENSION);

		runTests(testInputFiles, resultFiles, new ExecutableSpecTestHandler(Release.VDM_10, Dialect.VDM_RT), false);

		System.out.println("\n********");
		System.out.println("Finished with RT tests");
		System.out.println("********\n");		
	}

	private void runPrePostTests() throws IOException
	{
		System.out.println("Beginning pre/post-condition tests..\n");

		testInputFiles = TestUtils.getTestInputFiles(new File(PrePostTest.ROOT));
		resultFiles = TestUtils.getFiles(new File(PrePostTest.ROOT), RESULT_FILE_EXTENSION);

		runTests(testInputFiles, resultFiles, new ExecutableSpecTestHandler(Release.VDM_10, Dialect.VDM_PP), false);
		
		System.out.println("\n********");
		System.out.println("Finished with pre/post-condition tests");
		System.out.println("********\n");		
	}

	private void runBindTests() throws IOException
	{
		System.out.println("Beginning bind tests..\n");

		testInputFiles = TestUtils.getTestInputFiles(new File(BindTest.ROOT));
		resultFiles = TestUtils.getFiles(new File(BindTest.ROOT), RESULT_FILE_EXTENSION);

		runTests(testInputFiles, resultFiles, new ExecutableSpecTestHandler(Release.VDM_10, Dialect.VDM_PP), false);

		System.out.println("\n********");
		System.out.println("Finished with bind tests");
		System.out.println("********\n");
	}

	private void runConcurrencyClassicTests() throws IOException
	{
		System.out.println("Beginning concurrency classic tests..\n");

		testInputFiles = TestUtils.getTestInputFiles(new File(ConcurrencyClassicSpecTests.ROOT));
		resultFiles = TestUtils.getFiles(new File(ConcurrencyClassicSpecTests.ROOT), RESULT_FILE_EXTENSION);

		runTests(testInputFiles, resultFiles, new ExecutableSpecTestHandler(Release.CLASSIC, Dialect.VDM_PP), false);

		System.out.println("\n********");
		System.out.println("Finished with concurrency tests");
		System.out.println("********\n");

	}
	
	private void runConcurrencyTests() throws IOException
	{
		System.out.println("Beginning concurrency tests..\n");

		testInputFiles = TestUtils.getTestInputFiles(new File(ConcurrencyTests.ROOT));
		resultFiles = TestUtils.getFiles(new File(ConcurrencyTests.ROOT), RESULT_FILE_EXTENSION);

		runTests(testInputFiles, resultFiles, new ExecutableSpecTestHandler(Release.VDM_10, Dialect.VDM_PP), false);

		System.out.println("\n********");
		System.out.println("Finished with concurrency tests");
		System.out.println("********\n");
	}

	private void runUnionTests() throws IOException
	{
		System.out.println("Beginning union type tests..\n");

		testInputFiles = TestUtils.getTestInputFiles(new File(UnionTypeTest.ROOT));
		resultFiles = TestUtils.getFiles(new File(UnionTypeTest.ROOT), RESULT_FILE_EXTENSION);

		runTests(testInputFiles, resultFiles, new ExecutableSpecTestHandler(Release.VDM_10, Dialect.VDM_PP), false);

		System.out.println("\n********");
		System.out.println("Finished with union type tests");
		System.out.println("********\n");

	}

	private void runPatternTests() throws IOException
	{
		System.out.println("Beginning pattern tests..\n");

		testInputFiles = TestUtils.getTestInputFiles(new File(PatternTest.ROOT));
		resultFiles = TestUtils.getFiles(new File(PatternTest.ROOT), RESULT_FILE_EXTENSION);

		runTests(testInputFiles, resultFiles, new ExecutableSpecTestHandler(Release.VDM_10, Dialect.VDM_PP), false);

		System.out.println("\n********");
		System.out.println("Finished with pattern tests");
		System.out.println("********\n");
	}

	private void runConfiguredCloningTests() throws IOException
	{
		System.out.println("Beginning configured cloning tests..\n");

		testInputFiles = TestUtils.getTestInputFiles(new File(ConfiguredCloningTest.ROOT));
		resultFiles = TestUtils.getFiles(new File(ConfiguredCloningTest.ROOT), RESULT_FILE_EXTENSION);

		runTests(testInputFiles, resultFiles, new ExecutableSpecTestHandler(Release.CLASSIC, Dialect.VDM_PP), false);

		System.out.println("\n********");
		System.out.println("Finished with configured cloning tests");
		System.out.println("********\n");
	}

	private void runConfiguredStringTests() throws IOException
	{
		System.out.println("Beginning configured string tests..\n");

		testInputFiles = TestUtils.getTestInputFiles(new File(ConfiguredStringGenerationTest.ROOT));
		resultFiles = TestUtils.getFiles(new File(ConfiguredStringGenerationTest.ROOT), RESULT_FILE_EXTENSION);

		runTests(testInputFiles, resultFiles, new ExecutableSpecTestHandler(Release.VDM_10, Dialect.VDM_PP), false);

		System.out.println("\n********");
		System.out.println("Finished with configured string tests");
		System.out.println("********\n");
	}

	private void runFunctionValueTests() throws IOException
	{
		System.out.println("Beginning function value tests..\n");

		testInputFiles = TestUtils.getTestInputFiles(new File(FunctionValueTest.ROOT));
		resultFiles = TestUtils.getFiles(new File(FunctionValueTest.ROOT), RESULT_FILE_EXTENSION);

		runTests(testInputFiles, resultFiles, new ExecutableSpecTestHandler(Release.VDM_10, Dialect.VDM_PP), false);

		System.out.println("\n********");
		System.out.println("Finished with function value tests");
		System.out.println("********\n");
	}

	private void runNonExecutingVdm10Tests() throws IOException
	{
		System.out.println("Beginning with specification tests..");

		testInputFiles = TestUtils.getTestInputFiles(new File(SpecificationTest.ROOT));
		resultFiles = TestUtils.getFiles(new File(SpecificationTest.ROOT), RESULT_FILE_EXTENSION);

		runTests(testInputFiles, resultFiles, new NonExecutableSpecTestHandler(Release.VDM_10,Dialect.VDM_PP), false);

		System.out.println("\n********");
		System.out.println("Finished with specification tests");
		System.out.println("********\n");
	}

	private void runExecutingClassicSpecTests() throws IOException
	{
		System.out.println("Beginning classic specification tests..\n");

		testInputFiles = TestUtils.getTestInputFiles(new File(ClassicSpecTest.ROOT));
		resultFiles = TestUtils.getFiles(new File(ClassicSpecTest.ROOT), RESULT_FILE_EXTENSION);

		runTests(testInputFiles, resultFiles, new ExecutableSpecTestHandler(Release.CLASSIC, Dialect.VDM_PP), false);

		System.out.println("\n********");
		System.out.println("Finished with classic specification tests");
		System.out.println("********\n");
	}

	private void runComplexExpTests() throws IOException
	{
		System.out.println("Beginning complex expression tests..\n");

		testInputFiles = TestUtils.getTestInputFiles(new File(ComplexExpressionTest.ROOT));
		resultFiles = TestUtils.getFiles(new File(ComplexExpressionTest.ROOT), RESULT_FILE_EXTENSION);

		runTests(testInputFiles, resultFiles, new ExecutableSpecTestHandler(Release.VDM_10, Dialect.VDM_PP), false);

		System.out.println("\n********");
		System.out.println("Finished with complex expression tests");
		System.out.println("********\n");
	}

	private void runExpTests() throws IOException
	{
		System.out.println("Beginning expression tests..\n");

		testInputFiles = TestUtils.getTestInputFiles(new File(ExpressionTest.ROOT));
		resultFiles = TestUtils.getFiles(new File(ExpressionTest.ROOT), RESULT_FILE_EXTENSION);

		runTests(testInputFiles, resultFiles, new ExpressionTestHandler(Release.VDM_10, Dialect.VDM_PP), true);

		System.out.println("\n********");
		System.out.println("Finished with expression tests");
		System.out.println("********\n");
	}

	public static void addPath(File f)
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

	public void runTests(List<File> testInputFiles, List<File> resultFiles,
			TestHandler testHandler, boolean printInput) throws IOException
	{
		runTests(testInputFiles, resultFiles, testHandler, printInput, null);
	}

	public void runTests(List<File> testInputFiles, List<File> resultFiles,
			TestHandler testHandler, boolean printInput, String rootPackage) throws IOException
	{
		if (testInputFiles.size() != resultFiles.size())
		{
			throw new IllegalArgumentException("Number of test input files and number of result files differ");
		}

		final int testCount = testInputFiles.size();

		for (int i = 0; i < testCount; i++)
		{
			String resultFileName = resultFiles.get(i).getName();
			String inputFileName = testInputFiles.get(i).getName();

			if (!resultFileName.equals(inputFileName + RESULT_FILE_EXTENSION))
			{
				throw new IllegalArgumentException("Test input file and result file do not match. "
						+ "Test input file: "
						+ inputFileName
						+ ". Result file: " + resultFileName);
			}
		}

		File parent = new File(TEMP_DIR);
		parent.mkdirs();

		for (int i = 0; i < testCount; i++)
		{
			File currentInputFile = testInputFiles.get(i);
			testHandler.setCurrentInputFile(currentInputFile);
			
			GeneralUtils.deleteFolderContents(parent, FOLDER_NAMES_TO_AVOID, false);

			// Calculating the Java result:
			File currentResultFile = resultFiles.get(i);

			testHandler.setCurrentResultFile(currentResultFile);
			testHandler.writeGeneratedCode(parent, currentResultFile, rootPackage);


			boolean compileOk = JavaCommandLineCompiler.compile(parent, null);
			System.out.println("Test:" + testNumber + " (" + (1 + i) + "). Name: " + currentResultFile.getName()
					+ " " + (compileOk ? "Compile OK" : "ERROR"));

			if (!compileOk)
			{
				continue;
			}

			if (testHandler instanceof ExecutableTestHandler)
			{
				ExecutableTestHandler executableTestHandler = (ExecutableTestHandler) testHandler;

				// Calculating the VDM Result:
				Object vdmResult = null;
				
				try
				{
					vdmResult = executableTestHandler.interpretVdm(currentInputFile);
					
					if(vdmResult == null)
					{
						return;
					}
				} 
				catch (ContextException ce1)
				{
					vdmResult = ce1;
				}
				catch (Exception e1)
				{
					e1.printStackTrace();
					return;
				}
			
				ExecutionResult javaResult = executableTestHandler.runJava(parent);
				
				if(javaResult == null)
				{
					return;
				}
				
				boolean equal = false;
				
				if(vdmResult instanceof ContextException)
				{
					String cgValueStr = javaResult.getExecutionResult().toString();
					String vdmValueStr = ((ContextException) vdmResult).getMessage();
					
					equal = vdmValueStr.contains(cgValueStr);
				}
				else if(vdmResult instanceof ExecutionResult)
				{
					// Comparison of VDM and Java results
					ComparisonCG comp = new ComparisonCG(currentInputFile);
					equal = comp.compare(javaResult.getExecutionResult(), 
							((ExecutionResult) vdmResult).getExecutionResult());
				}
				else
				{
					System.err.println("Expected the VDM execution result to be of type ExecutionResult. Got: " + vdmResult);
					return;
				}

				if (printInput)
				{
					String vdmInput = GeneralUtils.readFromFile(currentInputFile);
					System.out.println("VDM:  " + vdmInput);

					String generatedCode = GeneralUtils.readFromFile(currentResultFile).replace('#', ' ');
					System.out.println("Java: " + generatedCode);
				} else
				{
					System.out.println("CG Test: " + currentInputFile.getName());
				}

				String vdmStrRep = vdmResult instanceof ExecutionResult ? 
						toShortString(((ExecutionResult) vdmResult).getStrRepresentation()) :
							toShortString(vdmResult); 
				
				System.out.println("VDM ~>  " + vdmStrRep);
				System.out.print("Java ~> " + toShortString(javaResult.getStrRepresentation()));

				if (equal)
				{
					System.out.println("Evaluation OK: VDM value and Java value are equal\n");
				} else
				{
					System.err.println("ERROR: VDM value and Java value are different");
				}
			}
			testNumber++;
		}
	}

	private String toShortString(Object result)
	{
		final int MAX = 1000;
		String str = result.toString();

		if (str.length() > MAX)
		{
			return str.substring(0, MAX) + "...\n";
		} else
		{
			return str;
		}
	}
}
