package org.overture.core.tests;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.overture.ast.node.INode;
import org.overture.core.tests.AllExamplesHelper.ExampleAstData;
import org.overture.parser.lex.LexException;
import org.overture.parser.syntax.ParserException;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@RunWith(Parameterized.class)
public abstract class ParamExamplesTest<R extends Serializable>
{

	String resultPath;
	List<INode> model;
	protected boolean updateResult = false;

	private final static String RESULTS_EXAMPLES = "src/test/resources/examples/";

	public ParamExamplesTest(String _, List<INode> model, String result)
	{
		this.model = model;
		this.resultPath = result;
	}

	@Test
	public void testCase() throws FileNotFoundException, IOException,
			ParserException, LexException
	{
		if (updateResult)
		{
			testUpdate();
		} else
		{
			R actual = processModel(model);
			R expected = deSerializeResult(resultPath);
			this.compareResults(actual, expected);
		}
	}

	@Parameters(name = "{index} : {0}")
	public static Collection<Object[]> testData() throws ParserException,
			LexException, IOException
	{
		Collection<ExampleAstData> examples = AllExamplesHelper.getExamplesAsts();
		Collection<Object[]> r = new Vector<Object[]>();

		for (ExampleAstData e : examples)
		{
			r.add(new Object[] { e.getExampleName(), e.getModel(),
					RESULTS_EXAMPLES + e.getExampleName() });
		}

		return r;
	}

	public abstract R processModel(List<INode> model);

	public abstract void compareResults(R actual, R expected);

	public R deSerializeResult(String resultPath2)
			throws FileNotFoundException, IOException
	{
		Gson gson = new Gson();
		Type resultType = getResultType();
		String json = IOUtils.toString(new FileReader(resultPath));
		R results = gson.fromJson(json, resultType);
		return results;
	}

	private Type getResultType()
	{
		Type resultType = new TypeToken<R>()
		{
		}.getType();
		return resultType;
	}

	private void testUpdate() throws ParserException, LexException, IOException
	{
		R actual = processModel(model);
		Gson gson = new Gson();
		String json = gson.toJson(actual);
		IOUtils.write(json, new FileOutputStream(resultPath));
	}

}
