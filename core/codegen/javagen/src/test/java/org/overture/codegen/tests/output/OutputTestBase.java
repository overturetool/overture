package org.overture.codegen.tests.output;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Type;
import java.util.List;

import org.junit.Assert;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.node.INode;
import org.overture.codegen.analysis.violations.UnsupportedModelingException;
import org.overture.codegen.ir.IRSettings;
import org.overture.codegen.utils.GeneralUtils;
import org.overture.codegen.utils.GeneratedData;
import org.overture.codegen.vdm2java.JavaCodeGen;
import org.overture.codegen.vdm2java.JavaSettings;
import org.overture.core.tests.ParamStandardTest;
import org.overture.parser.lex.LexException;
import org.overture.parser.syntax.ParserException;

import com.google.gson.reflect.TypeToken;

public abstract class OutputTestBase extends ParamStandardTest<String>
{
	protected static final String LINE_SEPARATOR = System.getProperty("line.separator");
	protected static final String QUOTE_SEPARATOR = ",";
	protected static final String MODULE_DELIMITER = LINE_SEPARATOR
			+ "##########" + LINE_SEPARATOR;
	protected static final String NAME_VIOLATION_INDICATOR = "*Name Violations*";
	protected static final String QUOTE_INDICATOR = "*Quotes*";

	public static final String UPDATE_PROPERTY_PREFIX = "tests.javagen.override.";
	public static final String UPDATE_ALL_OUTPUT_TESTS_PROPERTY = UPDATE_PROPERTY_PREFIX
			+ "all";

	protected static JavaCodeGen vdmCodGen = new JavaCodeGen();

	public OutputTestBase(String nameParameter, String inputParameter,
			String resultParameter)
	{
		super(nameParameter, inputParameter, resultParameter);
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

	abstract public GeneratedData genCode(List<INode> ast)
			throws AnalysisException, UnsupportedModelingException;

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
		Assert.assertEquals("Unexpected code produced by the Java code generator", expected.trim(), actual.trim());
	}

	@Override
	public String deSerializeResult(String resultPath)
			throws FileNotFoundException, IOException
	{
		return GeneralUtils.readFromFile(new File(resultPath));
	}

	@Override
	protected void testUpdate(String actual) throws ParserException,
			LexException, IOException
	{
		PrintStream out = new PrintStream(new FileOutputStream(new File(resultPath)));
		out.print(actual);
		out.close();
	}

	@Override
	protected boolean updateCheck()
	{
		if (super.updateCheck())
		{
			return true;
		}

		if (System.getProperty(UPDATE_ALL_OUTPUT_TESTS_PROPERTY) != null)
		{
			return true;
		}

		return false;
	}

	protected abstract String getUpdatePropertyString();
}