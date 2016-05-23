package org.overture.core.tests.demos;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.overture.ast.node.INode;
import org.overture.core.tests.ParamStandardTest;
import org.overture.core.tests.PathsProvider;

import com.google.gson.reflect.TypeToken;

/**
 * Simple demonstration of the overture test framework. Takes an AST and dumps the entire content into a string. The new
 * tests rely heavily on JUnit Parameterized tests so the <code>@RunWith</code> annotation is essential. The test will
 * not run otherwise.<br>
 * <br>
 * In addition, every tests in runs a specific result type that is fed to the test as a parameter, in
 * this case {@link SampleTestResult}.
 * 
 * @author ldc
 */
@RunWith(Parameterized.class)
public class SampleStandardTest extends ParamStandardTest<SampleTestResult>
{

	// Root location of the test input and result files
	private static final String EXAMPLE_TEST_FILES = "src/test/resources/demos";

	// The update property for this test
	private static final String UPDATE_PROPERTY = "tests.update.testing.samples.SampleStandardTest";

	/**
	 * The default constructor must always pass the name of the test and the paths of the test input and result. <br>
	 * <br>
	 * Anything else is up to you. In this case, we don't do anything else.
	 * 
	 * @param name
	 *            Name of the test. Usually derived from the input file.
	 * @param testInput
	 *            Path to the test input file.
	 * @param testResult
	 *            Path to the test result file
	 */
	public SampleStandardTest(String name, String testInput, String testResult)
	{
		super(name, testInput, testResult);
	}

	/**
	 * Due to some annoying limitations to JUnit parameterized tests, we must create a new test data provider for each
	 * test we create. In most of them, it should be enough to call {@link PathsProvider} and ask for the files. The
	 * <code>Parameters</code> annotation is crucial for the whole thing to work. Don't forget it.
	 * 
	 * @return the paths for the test inputs.
	 */
	@Parameters(name = "{index} : {0}")
	public static Collection<Object[]> testData()
	{
		return PathsProvider.computePaths(EXAMPLE_TEST_FILES);
	}


	/**
	 * This method is used in the main test case of the superclass to process the model and produce some kind of desired
	 * result. Most modules already have some kind of output so the easiest thing to do is to just write a conversion
	 * method between the output of your module and the new result type that you've created.
	 * </p>
	 * Our example is just the ID function so it doesn't do anything to the AST. But we do have a conversion method. It
	 * is placed in the result class so it can be reused in multiple tests that use {@link SampleTestResult}.
	 */
	@Override
	public SampleTestResult processModel(List<INode> ast)
	{
		SampleTestResult actual = SampleTestResult.convert(ast);
		return actual;
	}

	/**
	 * Here we return the type of our test result, to help the JSON deserialization. In general, you can always use this
	 * exact code and just place your result type in the Type Token.
	 * </p>
	 * The superclass system actually tries to work this type out on its own from the type parameter but it often fails
	 * since this is done with sophisticated reflection trickery.
	 */
	@Override
	public Type getResultType()
	{
		Type resultType = new TypeToken<SampleTestResult>()
		{
		}.getType();
		return resultType;
	}

	/**
	 * If we wish to update the test results, this is done through Java Properties (passed to the VM as arguments with
	 * <code>-D</code>. So, we must define a specific property for this set of tests that we can then use to update
	 * results.
	 */
	@Override
	protected String getUpdatePropertyString()
	{
		return UPDATE_PROPERTY;
	}

}
