package org.overture.codegen.tests.exec.util;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.overture.codegen.ir.IRSettings;
import org.overture.codegen.tests.exec.util.testhandlers.ExecutableTestHandler;
import org.overture.codegen.tests.exec.util.testhandlers.TestHandler;
import org.overture.codegen.tests.util.TestUtils;
import org.overture.codegen.utils.GeneralCodeGenUtils;
import org.overture.interpreter.runtime.ContextException;
import org.overture.test.framework.ConditionalIgnoreMethodRule;
import org.overture.test.framework.Properties;
import org.overture.test.framework.results.IMessage;
import org.overture.test.framework.results.Result;
import org.overture.typechecker.util.TypeCheckerUtil.TypeCheckResult;

public abstract class CheckerTestBase extends JavaCodeGenTestCase
{
	public static final String EXEC_TEST_PROPERTY = "tests.javagen.javac";
	protected TestHandler testHandler;
	protected File outputDir;

	public CheckerTestBase(File vdmSpec, TestHandler testHandler)
	{
		super(vdmSpec, null, null);
		this.testHandler = testHandler;
	}

	protected static Collection<Object[]> collectTests(File root,
			TestHandler handler)
	{
		Collection<Object[]> tests = new Vector<Object[]>();

		List<File> vdmSources = TestUtils.getTestInputFiles(root);

		final int testCount = vdmSources.size();

		for (int i = 0; i < testCount; i++)
		{
			File vdmSource = vdmSources.get(i);
			String name = vdmSource.getAbsolutePath().substring(root.getAbsolutePath().length() + 1);

			tests.add(new Object[] { name, vdmSource, handler });
		}

		return tests;
	}

	@Before
	public void setUp() throws Exception
	{
		outputDir = new File(new File(new File("target"), getClass().getSimpleName()), file.getName());
		outputDir.mkdirs();
	}

	@Rule
	public ConditionalIgnoreMethodRule rule = new ConditionalIgnoreMethodRule();

	public boolean runTest()
	{
		return System.getProperty(getExecProperty()) != null;
	}

	public String getExecProperty()
	{
		return EXEC_TEST_PROPERTY;
	}
	
	abstract public void genSourcesAndCompile();
	
	@Test
	public void test() throws Exception
	{
		Assume.assumeTrue("Pass property -D" + getExecProperty() + " to run test", runTest());
		
		assumeTest();
		
		configureResultGeneration();
		try
		{
			genSourcesAndCompile();

			if (testHandler instanceof ExecutableTestHandler)
			{
				Result<Object> result = produceResult();
				compareResults(result, file.getName() + ".eval.result");
			}
		} finally
		{
			unconfigureResultGeneration();
		}
	}
	
	public void assumeTest()
	{
		/* Allow all tests to run by default */
	}

	public static <T extends TypeCheckResult<?>> T checkTcResult(T tcResult)
	{
		if(GeneralCodeGenUtils.hasErrors(tcResult))
		{
			Assert.fail("Problems parsing/type checking VDM model:\n" + GeneralCodeGenUtils.errorStr(tcResult));
		}
		
		return tcResult;
	}

	public IRSettings getIrSettings()
	{
		IRSettings irSettings = new IRSettings();
		irSettings.setCharSeqAsString(false);

		return irSettings;
	}

	private Result<Object> produceResult() throws IOException
	{
		if (testHandler instanceof ExecutableTestHandler)
		{
			ExecutableTestHandler executableTestHandler = (ExecutableTestHandler) testHandler;
			if (Properties.recordTestResults)
			{
				Object vdmResult = evalVdm(file, executableTestHandler);
				return new Result<Object>(vdmResult, new Vector<IMessage>(), new Vector<IMessage>());
			}

			// Note that the classes returned in javaResult may be loaded by another class loader. This is the case for
			// classes representing VDM classes, Quotes etc. that's not part of the cg-runtime
			
			ExecutionResult javaResult = executableTestHandler.runJava(outputDir);

			if (javaResult == null)
			{
				Assert.fail("No Java result could be produced");
			}

			return new Result<Object>(javaResult, new Vector<IMessage>(), new Vector<IMessage>());

		}

		Assert.fail("Trying to produce result using an unsupported test handler: "
				+ testHandler);

		return new Result<Object>(null, new Vector<IMessage>(), new Vector<IMessage>());
	}

	public  File[] consCpFiles()
	{
		File cgRuntime = new File(org.overture.codegen.runtime.EvaluatePP.class.getProtectionDomain().getCodeSource().getLocation().getFile());
		return new File[]{cgRuntime};
	}

	public void compile(File[] cpJars)
	{
		ProcessResult result = JavaCommandLineCompiler.compile(outputDir, cpJars);
		Assert.assertTrue("Generated Java code did not compile: " + result.getOutput().toString(), result.getExitCode() == 0);
	}

	/**
	 * Evaluate the VDM specification, exceptions are returned as a String otherwise Value
	 * 
	 * @param currentInputFile
	 * @param executableTestHandler
	 * @return the result of the VDM execution
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
			// Context exceptions are used to report the result of erroneous VDM executions
			vdmResult = ce1.getMessage();
		} catch (Exception e1)
		{
			e1.printStackTrace();
			Assert.fail("Got unexpected exception when computing the VDM value");
		}

		return vdmResult;
	}

	@Override
	protected boolean assertEqualResults(Object expected, Object actual,
			PrintWriter out)
	{
		ExecutionResult javaResult = (ExecutionResult) actual;

		// Comparison of VDM and Java results
		ComparisonIR comp = new ComparisonIR(file);
		boolean equal = comp.compare(javaResult.getExecutionResult(), expected);

		if (!equal)
		{
			out.println(String.format("Actual result: %s does not match Expected: %s", ""
					+ actual, "" + expected));
		}

		return equal;
	}
}
