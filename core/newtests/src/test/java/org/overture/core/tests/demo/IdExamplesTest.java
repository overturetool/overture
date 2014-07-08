package org.overture.core.tests.demo;

import java.util.List;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.overture.ast.node.INode;
import org.overture.core.tests.examples.ParamExamplesTest;

@RunWith(Parameterized.class)
public class IdExamplesTest extends ParamExamplesTest<IdTestResult>
{

	public IdExamplesTest(String name, List<INode> model, String result)
	{
		super(name, model, result);
	}

	private static final String UPDATE_PROPERTY = "tests.update.example.ExamplesID";

	@Override
	public IdTestResult processModel(List<INode> model)
	{
		return IdTestResult.convert(model);
	}

	@Override
	public void compareResults(IdTestResult actual, IdTestResult expected)
	{
		IdTestResult.compare(actual, expected, testName);
	}

	@Override
	protected String getUpdatePropertyString()
	{
		return UPDATE_PROPERTY;
	}

}