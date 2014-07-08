package org.overture.core.tests.demos;

import java.util.List;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.overture.ast.node.INode;
import org.overture.core.tests.ParamExternalsTest;

/**
 * A very simple alternate version of {@link IdStandardTest} to work with external tests. We cannot directly reuse the
 * {@link IdStandardTest} since we must inherit {@link ParamExternalsTest}. But since we factor most of the important code out
 * to {@link IdTestResult}, this class is actually very small.<br>
 * <br>
 * Also note that since this test works with external inputs, the data provider is already set up in
 * {@link ParamExternalsTest}. To launch these tests simply use the property
 * <code>-DexternalTestsPath=/path/to/files/</code> .<br>
 * <br>
 * Due to some quirks with Parameterized JUnit tests, if the property is not set, the test will still launch, only with
 * 0 cases. It's fine on maven but in Eclipse you will get a single test run that does nothing. We're working on a way
 * to fix this.
 * 
 * @author ldc
 */
@RunWith(Parameterized.class)
public class IdExternalsTest extends ParamExternalsTest<IdTestResult>
{

	// the update property for this test
	private static final String UPDATE_PROPERTY = "tests.update.example.ExternalID";

	/**
	 * As usual in the new tests, the constructor only needs to pass the parameters up to super.
	 * 
	 * @param nameParameter
	 * @param testParameter
	 * @param resultParameter
	 */
	public IdExternalsTest(String nameParameter, String testParameter,
			String resultParameter)
	{
		super(nameParameter, testParameter, resultParameter);
	}

	/**
	 * Main comparison method. Simply call on {@link IdTestResult}.
	 */
	@Override
	public void testCompare(IdTestResult actual, IdTestResult expected)
	{
		IdTestResult.compare(actual, expected, testName);
	}

	/**
	 * Main model processing. Does nothing to the model and then converts it via {@link IdTestResult}.
	 */
	@Override
	public IdTestResult processModel(List<INode> ast)
	{
		IdTestResult actual = IdTestResult.convert(ast);
		return actual;
	}

	/**
	 * Return the update property for this test. In general, it's good practice to do put it in a constant and return
	 * that.
	 */
	@Override
	protected String getUpdatePropertyString()
	{
		return UPDATE_PROPERTY;
	}

}
