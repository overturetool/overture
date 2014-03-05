package org.overture.codegen.tests.utils;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Arrays;
import java.util.List;

import org.overture.codegen.constants.IJavaCodeGenConstants;
import org.overture.codegen.constants.IOoAstConstants;
import org.overture.codegen.tests.ComplexExpressionTest;
import org.overture.codegen.tests.ExpressionTest;
import org.overture.codegen.tests.SpecificationTest;
import org.overture.codegen.utils.GeneralCodeGenUtils;
import org.overture.codegen.utils.GeneralUtils;
import org.overture.interpreter.values.Value;

public class CompileTests
{
	private static final String CG_VALUE_BINARY_FILE = "target\\cgtest\\myData.bin";
	private static final String TEMP_DIR = "target\\cgtest";
	private static final String SRC_JAVA_LIB = "src\\main\\java\\org\\overture\\codegen\\javalib";
	private static final String TARGET_JAVA_LIB = "target\\cgtest\\org\\overture\\codegen\\javalib";
	
	private static final List<String> FOLDER_NAMES_TO_AVOID = Arrays.asList(new String[]{"javalib"});

	private static final String RESULT_FILE_EXTENSION = ".result";
	
	public static void main(String[] args) throws IOException
	{
		long startTimeMs = System.currentTimeMillis();
		
		File srcJavaLib = new File(SRC_JAVA_LIB);
		File utils = new File(TARGET_JAVA_LIB);
		
		GeneralCodeGenUtils.copyDirectory(srcJavaLib, utils);
		
		List<File> testInputFiles = TestUtils.getTestInputFiles(new File(ExpressionTest.ROOT));
		List<File>  resultFiles = TestUtils.getFiles(new File(ExpressionTest.ROOT), RESULT_FILE_EXTENSION);

		System.out.println("Beginning expressions..\n");
		
		runExpressionTests(testInputFiles, resultFiles, new ExpressionTestHandler(), true);
		
		System.out.println("\n********");
		System.out.println("Finished with expressions");
		System.out.println("********\n");
		
		testInputFiles = TestUtils.getTestInputFiles(new File(ComplexExpressionTest.ROOT));
		resultFiles = TestUtils.getFiles(new File(ComplexExpressionTest.ROOT), RESULT_FILE_EXTENSION);
		
		System.out.println("Beginning complex expressions..\n");
		
		runExpressionTests(testInputFiles, resultFiles, new ComplexExpressionTestHandler(), false);
		
		System.out.println("\n********");
		System.out.println("Finished with complex expressions");
		System.out.println("********\n");
		
		System.out.println("Beginning with specifications..");
		
		runSpecificationTests();
		
		System.out.println("\n********");
		System.out.println("Finished with specifications");
		System.out.println("********\n");
		
		long endTimeMs = System.currentTimeMillis();
		
		long totalTimeMs = (endTimeMs - startTimeMs);
		
		long minutes = totalTimeMs / (60 * 1000);
		long seconds = (totalTimeMs % (60 * 1000)) / 1000;
		
		System.out.println("Time: " + String.format("%02d:%02d", minutes, seconds) + ".");
	}

	private static void runSpecificationTests() throws IOException
	{
		List<File> resultFiles = TestUtils.getFiles(new File(SpecificationTest.ROOT), RESULT_FILE_EXTENSION);

		File parent = new File(TEMP_DIR);

		for (int i = 0; i < resultFiles.size(); i++)
		{
			GeneralUtils.deleteFolderContents(parent, FOLDER_NAMES_TO_AVOID);
			File file = resultFiles.get(i);

			if (!file.getName().endsWith(RESULT_FILE_EXTENSION))
				continue;

			List<StringBuffer> content = TestUtils.readJavaModulesFromResultFile(file);

			if (content.size() == 0)
			{
				System.out.println("Got no clases for: " + file.getName());
				continue;
			}

			parent.mkdirs();

			for (StringBuffer classCgStr : content)
			{
				String className = TestUtils.getJavaModuleName(classCgStr);
				File outputDir = parent;

				if (className.equals(IOoAstConstants.QUOTES_INTERFACE_NAME))
				{
					outputDir = new File(parent, IOoAstConstants.QUOTES_INTERFACE_NAME);
					outputDir.mkdirs();
				}

				File tempFile = new File(outputDir, className
						+ IJavaCodeGenConstants.JAVA_FILE_EXTENSION);

				if (!tempFile.exists())
				{
					tempFile.createNewFile();
				}

				FileWriter xwriter = new FileWriter(tempFile);
				xwriter.write(classCgStr.toString());
				xwriter.flush();
				xwriter.close();
			}
				
			System.out.println("Test: " + (1 + i) + ". " + (JavaCommandLineCompiler.compile(parent, null) ? "Compile OK" :"ERROR") + ": " + file.getName());
		}
	}
	
	public static void runExpressionTests(List<File> testInputFiles, List<File> resultFiles, TestHandler resultWriter, boolean printInput) throws IOException
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
			GeneralUtils.deleteFolderContents(parent, FOLDER_NAMES_TO_AVOID);
			
			//Calculating the VDM Result:
			File currentInputFile = testInputFiles.get(i);
			Value vdmResult;
			
			try
			{
				vdmResult = resultWriter.interpretVdm(currentInputFile);
			} catch (Exception e1)
			{
				e1.printStackTrace();
				return;
			}

			//Calculating the Java result:
			File file = resultFiles.get(i);
			
			String generatedCode = GeneralUtils.readFromFile(file).replace('#', ' ');
			
			resultWriter.writeGeneratedCode(parent, generatedCode);
			
			boolean compileOk = JavaCommandLineCompiler.compile(parent, null);
			System.out.println("Test:" + (1 + i)  + ". Name: " + file.getName() + " " + (compileOk ? "Compile OK" : "ERROR"));

			if(!compileOk)
				continue;
			
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
			}
			finally
			{
				ois.close();
			}
			
			//Comparison of VDM and Java results
			boolean equal = ComparisonCG.compare(cgValue, vdmResult);
			
			if (printInput)
			{
				String vdmInput = GeneralUtils.readFromFile(currentInputFile);

				System.out.println("VDM:  " + vdmInput);
				System.out.println("Java: " + generatedCode);
			}
			else
			{
				
				System.out.println("CG Test: " + currentInputFile.getName());
			}
			
			System.out.println("VDM ~>  " + vdmResult);
			System.out.print("Java ~> " + javaResult);
			System.out.println("\n");
			
			if(!equal)
			{
				System.err.println("ERROR: VDM value and Java value are different");
			}
		}
	}
}
