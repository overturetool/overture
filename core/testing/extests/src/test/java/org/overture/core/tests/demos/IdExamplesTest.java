package org.overture.core.tests.demos;

import java.lang.reflect.Type;
import java.util.List;

import junitparams.JUnitParamsRunner;

import org.junit.runner.RunWith;
import org.overture.ast.node.INode;
import org.overture.core.tests.examples.ParamExamplesTest;

import com.google.gson.reflect.TypeToken;

/**
 * Demonstration of new Overture tests on examples. Takes an AST and dumps the
 * entire content to a string. <br>
 * <br>
 * This test operates on result type {@link ExampleIdTestResult}.
 * 
 * @author ldc
 */
// @Ignore
@RunWith(JUnitParamsRunner.class)
public class IdExamplesTest extends ParamExamplesTest<ExampleIdTestResult> {



	private static final String UPDATE_PROPERTY = "tests.update.example.ExamplesID";

	@Override
	public ExampleIdTestResult processModel(List<INode> model) {
		return new ExampleIdTestResult(model, testName);
	}

	@Override
	public void compareResults(ExampleIdTestResult actual,
			ExampleIdTestResult expected) {
		ExampleIdTestResult.compare(actual, expected, testName);
	}

	/**
	 * Get the property that signals update mode. Always a good idea to return a
	 * constant instead of mainlining it.
	 */
	@Override
	protected String getUpdatePropertyString() {
		return UPDATE_PROPERTY;
	}

	/**
	 * Result type information for this test. Helps the main test driver along.
	 */
	@Override
	public Type getResultType() {
		Type resultType = new TypeToken<ExampleIdTestResult>() {
		}.getType();
		return resultType;

	}

	private static String EXAMPLES_ROOT = "../../../externals/examples/target/classes/";

	/**
	 * Path to the examples. Needs to be customized on a per-module basis.
	 */
	@Override
	protected String getRelativeExamplesPath() {
		return EXAMPLES_ROOT;
	}

}
