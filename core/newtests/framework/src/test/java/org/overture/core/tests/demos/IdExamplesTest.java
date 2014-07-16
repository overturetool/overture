package org.overture.core.tests.demos;

import java.lang.reflect.Type;
import java.util.List;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.overture.ast.node.INode;
import org.overture.core.tests.ParamExamplesTest;

import com.google.gson.reflect.TypeToken;

/**
 * Demonstration of new Overture tests on examples. Takes an AST and dumps the entire content to a string. <br>
 * <br>
 * This test operates on result type {@link IdTestResult}.
 *
 * @author ldc
 */
@RunWith(Parameterized.class)
public class IdExamplesTest extends ParamExamplesTest<IdTestResult>
{

	/**
	 * Constructor just passes parameters along to the super. In the case of examples testing, this is particularly
	 * important. You should avoid constructor with additional parameters.
	 * 
	 * @param name
	 * @param model
	 * @param result
	 */
	public IdExamplesTest(String name, List<INode> model, String result)
	{
		super(name, model, result);
	}

	// the system property to signal running the test in update mode.
	private static final String UPDATE_PROPERTY = "tests.update.example.ExamplesID";

	/**
	 * Process the model. Since this is an ID test, it does nothing to the model and just converts it straight away to
	 * an {@link IdTestResult}.
	 */
	@Override
	public IdTestResult processModel(List<INode> model)
	{
		return IdTestResult.convert(model);
	}

	/**
	 * Compare results for this test. Relies on {@link IdTestResult#compare(IdTestResult, IdTestResult, String)}.
	 */
	@Override
	public void compareResults(IdTestResult actual, IdTestResult expected)
	{
		IdTestResult.compare(actual, expected, testName);
	}

	/**
	 * Get the property that signals update mode. Always a good idea to return a constant instead of mainlining it.
	 */
	@Override
	protected String getUpdatePropertyString()
	{
		return UPDATE_PROPERTY;
	}

	/**
	 * Result type information for this test. Helps the main test driver along.
	 */
	@Override
	public Type getResultType()
	{
		Type resultType = new TypeToken<IdTestResult>()
		{
		}.getType();
		return resultType;

	}

}