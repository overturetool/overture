package org.overture.codegen.execution.tests;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.overture.ast.analysis.AnalysisException;
import org.overture.codegen.ir.IRSettings;
import org.overture.codegen.tests.exec.ComparisonCG;
import org.overture.codegen.tests.exec.CompileTests;
import org.overture.codegen.tests.exec.ExecutableTestHandler;
import org.overture.codegen.tests.exec.ExecutionResult;
import org.overture.codegen.tests.exec.JavaCommandLineCompiler;
import org.overture.codegen.tests.exec.TestHandler;
import org.overture.codegen.tests.exec.TestUtils;
import org.overture.codegen.utils.GeneralUtils;
import org.overture.codegen.vdm2java.JavaCodeGen;
import org.overture.codegen.vdm2java.JavaCodeGenUtil;
import org.overture.codegen.vdm2java.JavaSettings;
import org.overture.config.Settings;
import org.overture.interpreter.runtime.ContextException;
import org.overture.interpreter.values.Value;
import org.overture.test.framework.ConditionalIgnoreMethodRule;
import org.overture.test.framework.ConditionalIgnoreMethodRule.ConditionalIgnore;
import org.overture.test.framework.Properties;
import org.overture.test.framework.results.IMessage;
import org.overture.test.framework.results.Result;

public abstract class CommonJavaGenCheckerTest extends JavaCodeGenTestCase
{
	protected static Collection<Object[]> collectTests(File root,
			TestHandler handler)
	{
		Collection<Object[]> tests = new Vector<Object[]>();

		// File root = new File(ExpressionTest.ROOT);
		List<File> vdmSources = TestUtils.getTestInputFiles(root);

		final int testCount = vdmSources.size();

		for (int i = 0; i < testCount; i++)
		{
			File vdmSource = vdmSources.get(i);
			File generatedJavaDataFile = new File(vdmSource.getParentFile(), vdmSource.getName()
					+ CompileTests.RESULT_FILE_EXTENSION);

			if (!generatedJavaDataFile.exists())
			{
				throw new IllegalArgumentException("Test VDM source does not have a generated Java data file: "
						+ generatedJavaDataFile);
			}

			String name = vdmSource.getAbsolutePath().substring(root.getAbsolutePath().length() + 1);

			tests.add(new Object[] { name, vdmSource, generatedJavaDataFile,
					handler, true, null });

			// if(i>2)
			// break;
		}

		return tests;
	}

	TestHandler testHandler;
	File javaGeneratedFile;
	boolean printInput;
	String rootPackage;

	public CommonJavaGenCheckerTest(File vdmSpec, File javaGeneratedFiles,
			TestHandler testHandler, boolean printInput, String rootPackage)
	{
		super(vdmSpec, null, null);
		this.testHandler = testHandler;
		this.javaGeneratedFile = javaGeneratedFiles;
		this.printInput = printInput;
		this.rootPackage = rootPackage;
	}

	@Before
	public void setUp() throws Exception
	{
		testHandler.initVdmEnv();

		outputDir = new File(new File(new File("target"), getClass().getSimpleName()), file.getName());
		outputDir.mkdirs();
	}

	@Rule
	public ConditionalIgnoreMethodRule rule = new ConditionalIgnoreMethodRule();

	@Test
	@ConditionalIgnore(condition = JavaCodeGenJavacEnabledCondition.class)
	public void test() throws Exception
	{
		configureResultGeneration();
		try
		{
			Result<Object> result = runJavaGenTest();

			compareResults(result, file.getName() + ".eval.result");
		} finally
		{
			unconfigureResultGeneration();
		}
	}

	File outputDir;

	protected void generateJavaSources(File vdmSource)
	{
		try
		{
			restoreGeneratedJavaCode();
		} catch (IOException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		if (true)
		{
			return;
		}

		// Settings.release = Release.VDM_10;
		// Dialect dialect = Dialect.VDM_PP;

		IRSettings irSettings = new IRSettings();
		irSettings.setCharSeqAsString(false);
		irSettings.setGeneratePreConds(false);
		irSettings.setGeneratePreCondChecks(false);
		irSettings.setGeneratePostConds(false);
		irSettings.setGeneratePostCondChecks(false);

		JavaSettings javaSettings = new JavaSettings();
		javaSettings.setDisableCloning(false);

		JavaCodeGen vdmCodGen = new JavaCodeGen();
		vdmCodGen.setSettings(irSettings);
		vdmCodGen.setJavaSettings(javaSettings);
		// List<File> tmp = new Vector<File>();
		// tmp.add(vdmSource);
		// JavaCodeGenMain.handleOo(tmp, irSettings, javaSettings, Settings.dialect, false, outputDir);
		//

		String fileContent;
		try
		{
			fileContent = GeneralUtils.readFromFile(file);
			String generatedJava = JavaCodeGenUtil.generateJavaFromExp(fileContent, vdmCodGen, Settings.dialect).getContent().trim();
			System.out.println(generatedJava);
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AnalysisException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private Result<Object> runJavaGenTest() throws IOException
	{
		generateJavaSources(file);

		File cgRuntime = new File(org.overture.codegen.runtime.EvaluatePP.class.getProtectionDomain().getCodeSource().getLocation().getFile());

		boolean compileOk = JavaCommandLineCompiler.compile(outputDir, cgRuntime);

		if (!compileOk)
		{
			return null;
		}

		if (testHandler instanceof ExecutableTestHandler)
		{
			ExecutableTestHandler executableTestHandler = (ExecutableTestHandler) testHandler;
			// Properties.recordTestResults=true;
			if (Properties.recordTestResults)
			{
				Object vdmResult = evalVdm(file, executableTestHandler);
				return new Result<Object>(vdmResult, new Vector<IMessage>(), new Vector<IMessage>());
			}

			// Note that the classes returned in javaResult may be loaded by another class loader. This is the case for
			// classes representing VDM classes, Quotes etc. that's not part of the cg-runtime
			ExecutionResult javaResult = executableTestHandler.runJava(outputDir);
			// System.out.println(" + java: "+(System.currentTimeMillis()-s));

			if (javaResult == null)
			{
				Assert.fail("no java result");
			}
			return new Result<Object>(javaResult, new Vector<IMessage>(), new Vector<IMessage>());

		}

		return new Result<Object>(null, new Vector<IMessage>(), new Vector<IMessage>());
	}

	private void restoreGeneratedJavaCode() throws IOException
	{
		testHandler.setCurrentInputFile(file);
		testHandler.setCurrentResultFile(javaGeneratedFile);
		testHandler.writeGeneratedCode(outputDir, javaGeneratedFile, rootPackage);
	}

	/**
	 * Evaluate the VDM specification, exceptions are returned as a String otherwise Value
	 * 
	 * @param currentInputFile
	 * @param executableTestHandler
	 * @return
	 */
	private Object evalVdm(File currentInputFile,
			ExecutableTestHandler executableTestHandler)
	{
		// Calculating the VDM Result:
		Object vdmResult = null;

		try
		{
			ExecutionResult res = executableTestHandler.interpretVdm(currentInputFile);

			if (res == null)
			{
				Assert.fail("no vdm result");
			}

			vdmResult = res.getExecutionResult();
		} catch (ContextException ce1)
		{
			vdmResult = ce1.getMessage();
		} catch (Exception e1)
		{
			e1.printStackTrace();
			Assert.fail();
		}
		return vdmResult;
	}

	@Override
	protected boolean assertEqualResults(Object expected, Object actual,
			PrintWriter out)
	{
		boolean equal = false;

		ExecutionResult javaResult = (ExecutionResult) actual;

		if (!(expected instanceof Value))
		{
			String cgValueStr = javaResult.getExecutionResult().toString();
			equal = expected.toString().contains(cgValueStr);

			if (!equal)
			{
				out.println(String.format("Actual result: '%s' is not compatible with Expected: '%s'", ""
						+ cgValueStr, "" + expected));
			}
		} else
		{

			Value vdmResult = (Value) expected;
			// Comparison of VDM and Java results
			ComparisonCG comp = new ComparisonCG(file);
			equal = comp.compare(javaResult.getExecutionResult(), vdmResult);

			if (!equal)
			{
				out.println(String.format("Actual result: %s does not match Expected: %s", ""
						+ actual, "" + expected));
			}
		}

		if (printInput)
		{
			// String vdmInput;
			// try
			// {
			// vdmInput = GeneralUtils.readFromFile(file);
			//
			// System.out.println("VDM:  " + vdmInput);
			//
			// String generatedCode = GeneralUtils.readFromFile(javaGeneratedFiles).replace('#', ' ');
			// System.out.println("Java: " + generatedCode);
			// } catch (IOException e)
			// {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
		}

		return equal;
	}

}
