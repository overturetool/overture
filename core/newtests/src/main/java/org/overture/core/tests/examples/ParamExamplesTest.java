package org.overture.core.tests.examples;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.overture.ast.node.INode;
import org.overture.core.tests.AbsResultTest;
import org.overture.parser.lex.LexException;
import org.overture.parser.syntax.ParserException;

@RunWith(Parameterized.class)
public abstract class ParamExamplesTest<R extends Serializable> extends
		AbsResultTest<R>
{

	String resultPath;
	List<INode> model;
	protected String testName;
	private final boolean updateResult;

	private final static String RESULTS_EXAMPLES = "src/test/resources/examples/";

	public ParamExamplesTest(String name, List<INode> model, String result)
	{
		this.testName=name;
		this.model = model;
		this.resultPath = result;
		this.updateResult = updateCheck();
	}

	@Test
	public void testCase() throws FileNotFoundException, IOException,
			ParserException, LexException
	{

		R actual = processModel(model);
		R expected = deSerializeResult(resultPath);
		if (updateResult)
		{
			testUpdate(actual);
		} else
		{
			this.compareResults(actual, expected);
		}
	}

	@Parameters(name = "{index} : {0}")
	public static Collection<Object[]> testData() throws ParserException,
			LexException, IOException
	{
		Collection<ExampleAstData> examples = ExamplesUtility.getExamplesAsts();
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

}
