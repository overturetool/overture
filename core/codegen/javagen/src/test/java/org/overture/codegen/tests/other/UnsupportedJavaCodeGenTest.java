package org.overture.codegen.tests.other;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.lex.Dialect;
import org.overture.ast.modules.AModuleModules;
import org.overture.codegen.ir.CodeGenBase;
import org.overture.codegen.tests.util.TestUtils;
import org.overture.codegen.utils.GeneralCodeGenUtils;
import org.overture.codegen.utils.GeneratedData;
import org.overture.codegen.utils.GeneratedModule;
import org.overture.codegen.vdm2java.JavaCodeGen;
import org.overture.config.Release;
import org.overture.config.Settings;
import org.overture.typechecker.util.TypeCheckerUtil;
import org.overture.typechecker.util.TypeCheckerUtil.TypeCheckResult;

@RunWith(Parameterized.class)
public class UnsupportedJavaCodeGenTest
{
	public static final String TEST_INPUT_FOLDER_PATH = "src"
			+ File.separatorChar + "test" + File.separatorChar + "resources"
			+ File.separatorChar + "unsupported";

	public static final String VDMSL_FILE_TEXT = ".vdmsl";

	private JavaCodeGen javaGen;

	private File testInputFile;

	public UnsupportedJavaCodeGenTest(File testInputFile)
	{
		this.testInputFile = testInputFile;
	}

	@Parameters(name = "{index}: {0}")
	public static Collection<Object[]> data()
	{
		return TestUtils.collectFiles(TEST_INPUT_FOLDER_PATH);
	}

	@Before
	public void init()
	{
		Settings.dialect = getDialect();
		Settings.release = Release.VDM_10;
		this.javaGen = new JavaCodeGen();
	}

	public Dialect getDialect()
	{
		if (testInputFile.getName().endsWith(VDMSL_FILE_TEXT))
		{
			return Dialect.VDM_SL;
		} else
		{
			return Dialect.VDM_PP;
		}
	}

	@Test
	public void test()
	{
		List<File> files = new LinkedList<File>();
		files.add(testInputFile);

		try
		{
			GeneratedData genData = genData(files);

			int noOfUnsupportedModules = 0;

			for (GeneratedModule mod : genData.getClasses())
			{
				if (!mod.getUnsupportedInIr().isEmpty())
				{
					// Sometimes a renamed definitions may also appear implicitly in the default module
					if (!(Settings.dialect == Dialect.VDM_SL
							&& mod.getName().equals("DEFAULT")))
					{
						noOfUnsupportedModules++;
					}

					Assert.assertEquals("Expected only a single unsupported construct to be reported", mod.getUnsupportedInIr().size(), 1);
					Assert.assertEquals("Expected no content to be generated", mod.getContent(), null);
				}
			}

			Assert.assertTrue("Expected a single module to be unsupported. "
					+ "Got: "
					+ noOfUnsupportedModules, noOfUnsupportedModules == 1);

		} catch (AnalysisException e)
		{
			Assert.assertTrue("Could not parse VDM-SL model", false);
		}
	}

	private GeneratedData genData(List<File> files) throws AnalysisException
	{
		if (Settings.dialect == Dialect.VDM_SL)
		{
			TypeCheckResult<List<AModuleModules>> tcResult = TypeCheckerUtil.typeCheckSl(files);
			validateTcResult(tcResult);

			return javaGen.generate(CodeGenBase.getNodes(tcResult.result));
		} else if (Settings.dialect == Dialect.VDM_PP)
		{
			TypeCheckResult<List<SClassDefinition>> tcResult = TypeCheckerUtil.typeCheckPp(files);
			validateTcResult(tcResult);

			return javaGen.generate(CodeGenBase.getNodes(tcResult.result));
		} else
		{
			Assert.fail("Only VDM-SL and VDM++ are supported for this test");
			return null;
		}
	}

	private void validateTcResult(TypeCheckResult<?> tcResult)
	{
		if (GeneralCodeGenUtils.hasErrors(tcResult))
		{
			Assert.fail("Could not parse/type check VDM model:\n"
					+ GeneralCodeGenUtils.errorStr(tcResult));
		}
	}
}
