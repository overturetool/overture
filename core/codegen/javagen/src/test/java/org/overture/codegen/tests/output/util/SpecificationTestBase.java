package org.overture.codegen.tests.output.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import org.junit.Assert;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.node.INode;
import org.overture.codegen.analysis.violations.InvalidNamesResult;
import org.overture.codegen.ir.IRSettings;
import org.overture.codegen.utils.GeneralCodeGenUtils;
import org.overture.codegen.utils.GeneratedData;
import org.overture.codegen.utils.GeneratedModule;
import org.overture.codegen.vdm2java.JavaCodeGen;
import org.overture.codegen.vdm2java.JavaSettings;
import org.overture.core.tests.ParamStandardTest;
import org.overture.parser.lex.LexException;
import org.overture.parser.syntax.ParserException;

import com.google.gson.reflect.TypeToken;

public abstract class SpecificationTestBase extends ParamStandardTest<String>
{
	protected static final String LINE_SEPARATOR = System.getProperty("line.separator");
	protected static final String QUOTE_SEPARATOR = ",";
	protected static final String MODULE_DELIMITER = LINE_SEPARATOR
			+ "##########" + LINE_SEPARATOR;
	protected static final String NAME_VIOLATION_INDICATOR = "*Name Violations*";
	protected static final String QUOTE_INDICATOR = "*Quotes*";

	public SpecificationTestBase(String nameParameter, String inputParameter,
			String resultParameter)
	{
		super(nameParameter, inputParameter, resultParameter);
	}
	
	public JavaCodeGen getJavaGen()
	{
		JavaCodeGen javaGen = new JavaCodeGen();
		javaGen.setSettings(getIrSettings());
		javaGen.setJavaSettings(getJavaSettings());
		
		return javaGen;
	}
	
	public IRSettings getIrSettings()
	{
		IRSettings irSettings = new IRSettings();
		irSettings.setCharSeqAsString(false);

		return irSettings;
	}

	public JavaSettings getJavaSettings()
	{
		JavaSettings javaSettings = new JavaSettings();
		javaSettings.setDisableCloning(false);

		return javaSettings;
	}
	
	@Override
	public String processModel(List<INode> ast)
	{
		try
		{
			StringBuilder generatedCode = new StringBuilder();
			GeneratedData data = genCode(ast);
			List<GeneratedModule> classes = data.getClasses();

			for (GeneratedModule classCg : classes)
			{
				generatedCode.append(classCg.getContent());
				generatedCode.append(MODULE_DELIMITER);
			}

			List<GeneratedModule> quoteData = data.getQuoteValues();

			if (quoteData != null && !quoteData.isEmpty())
			{
				generatedCode.append(QUOTE_INDICATOR + LINE_SEPARATOR);
				for (int i = 0; i < quoteData.size(); i++)
				{
					GeneratedModule q = quoteData.get(i);

					generatedCode.append(q.getName());

					if (i + 1 < quoteData.size())
					{
						generatedCode.append(QUOTE_SEPARATOR);
					}

				}
				generatedCode.append(MODULE_DELIMITER);
			}

			InvalidNamesResult invalidNames = data.getInvalidNamesResult();

			if (invalidNames != null && !invalidNames.isEmpty())
			{
				generatedCode.append(NAME_VIOLATION_INDICATOR + LINE_SEPARATOR);
				generatedCode.append(LINE_SEPARATOR
						+ GeneralCodeGenUtils.constructNameViolationsString(invalidNames));
				generatedCode.append(MODULE_DELIMITER);
			}

			return generatedCode.toString();

		}
		catch (AnalysisException e)
		{
			Assert.fail("Unexpected problem encountered when attempting to code generate VDM model: "
					+ e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	abstract public GeneratedData genCode(List<INode> ast)
			throws AnalysisException;

	@Override
	public Type getResultType()
	{
		Type resultType = new TypeToken<String>()
		{
		}.getType();
		return resultType;
	}

	@Override
	public void compareResults(String actual, String expected)
	{
		OutputTestUtil.compare(expected, actual);
	}

	@Override
	public String deSerializeResult(String resultPath)
			throws FileNotFoundException, IOException
	{
		return OutputTestUtil.deSerialize(resultPath);
	}

	@Override
	protected void testUpdate(String actual) throws ParserException,
			LexException, IOException
	{
		OutputTestUtil.testUpdate(actual, resultPath);
	}

	@Override
	protected boolean updateCheck()
	{
		if (frameworkUpdateCheck())
		{
			return true;
		}

		if (System.getProperty(OutputTestUtil.UPDATE_ALL_OUTPUT_TESTS_PROPERTY) != null)
		{
			return true;
		}

		return false;
	}

	public boolean frameworkUpdateCheck()
	{
		return super.updateCheck();
	}

	protected abstract String getUpdatePropertyString();
}