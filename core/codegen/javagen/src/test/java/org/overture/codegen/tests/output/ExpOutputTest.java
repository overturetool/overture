package org.overture.codegen.tests.output;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.lex.Dialect;
import org.overture.codegen.tests.output.util.OutputTestUtil;
import org.overture.codegen.utils.GeneralUtils;
import org.overture.codegen.vdm2java.JavaCodeGen;
import org.overture.codegen.vdm2java.JavaCodeGenUtil;
import org.overture.config.Release;
import org.overture.config.Settings;
import org.overture.core.testing.ParamFineGrainTest;
import org.overture.core.testing.PathsProvider;
import org.overture.parser.lex.LexException;
import org.overture.parser.syntax.ParserException;

import com.google.gson.reflect.TypeToken;

@RunWith(Parameterized.class)
public class ExpOutputTest extends ParamFineGrainTest<String>
{
	public static final String ROOT = "src" + File.separatorChar + "test"
			+ File.separatorChar + "resources" + File.separatorChar
			+ "expressions";

	public static final JavaCodeGen javaCodeGen = new JavaCodeGen();

	public ExpOutputTest(String nameParameter, String testParameter,
			String resultParameter)
	{
		super(nameParameter, testParameter, resultParameter);
	}

	@BeforeClass
	public static void init()
	{
		Settings.dialect = Dialect.VDM_PP;
		Settings.release = Release.VDM_10;
	}

	@Override
	public String processSource()
	{
		try
		{
			String fileContent = GeneralUtils.readFromFile(new File(modelPath));
			String generatedJava = JavaCodeGenUtil.generateJavaFromExp(fileContent, javaCodeGen, Settings.dialect).getContent().trim();
			String trimmed = GeneralUtils.cleanupWhiteSpaces(generatedJava);

			return trimmed;
		} catch (IOException | AnalysisException e)
		{
			e.printStackTrace();
			Assert.fail("Problems code generating expression to Java: "
					+ e.getMessage());
			return null;
		}
	}

	@Override
	public Type getResultType()
	{
		Type resultType = new TypeToken<String>()
		{
		}.getType();
		return resultType;
	}

	@Parameters(name = "{index} : {0}")
	public static Collection<Object[]> testData()
	{
		return PathsProvider.computePaths(ROOT);
	}

	@Override
	protected String getUpdatePropertyString()
	{
		return OutputTestUtil.UPDATE_PROPERTY_PREFIX + "exp";
	}

	@Override
	public void compareResults(String actual, String expected)
	{
		OutputTestUtil.compare(expected, actual);
	}

	@Override
	protected void testUpdate(String actual)
			throws ParserException, LexException, IOException
	{
		OutputTestUtil.testUpdate(actual, resultPath);
	}

	@Override
	public String deSerializeResult(String resultPath)
			throws FileNotFoundException, IOException
	{
		return OutputTestUtil.deSerialize(resultPath);
	}

	@Override
	protected boolean updateCheck()
	{
		if (super.updateCheck())
		{
			return true;
		}

		if (System.getProperty(OutputTestUtil.UPDATE_ALL_OUTPUT_TESTS_PROPERTY) != null)
		{
			return true;
		}

		return false;
	}
}
