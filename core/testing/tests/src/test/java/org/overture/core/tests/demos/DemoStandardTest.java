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
 * Quick usage demonstration of new overture tests. Takes an AST and dumps the entire content into a string. The new
 * tests rely heavily on JUnit Parameterized tests so the <code>@RunWith</code> annotation is essential. The test will
 * not run otherwise.<br>
 * <br>
 * In addition, every tests in this new system runs a specific result type that is fed to the test as a parameter, in
 * this case {@link IdTestResult}.
 * 
 * @author ldc
 */
@RunWith(Parameterized.class)
public class DemoStandardTest extends ParamStandardTest<IdTestResult>
{

	// Root location of the test input and result files
	private static final String EXAMPLE_TEST_FILES = "src/test/resources/demos";

	// The update property for this test
	private static final String UPDATE_PROPERTY = "tests.update.testing.tests.DemoStandardTest";

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
	public DemoStandardTest(String name, String testInput, String testResult)
	{
		super(name, testInput, testResult);
	}

	/**
	 * Due to some annoying limitations to JUnit parameterized tests, we must create a new test data provider for each
	 * test we create. In most of them, it should be enough to call {@link PathsProvider} and ask for the files. The
	 * <code> Parameters</code> annotation is crucial for the whole thing to work. Don't forget it.
	 * 
	 * @return
	 */
	@Parameters(name = "{index} : {0}")
	public static Collection<Object[]> testData()
	{
		return PathsProvider.computePaths(EXAMPLE_TEST_FILES);
	}

	/**
	 * This method holds the comparison logic between 2 test results. Obviously, it must always be implemented. In our
	 * case, we simply go through the generated strings for both results and fail the test in case they do not match. <br>
	 * <br>
	 * It's worth nothing that the responsibility to assert something is entirely on the user and this is the place to
	 * put the check in.<br>
	 * <br>
	 * It's a good idea to implement the comparison as a public static method somewhere. That way you can reuse in
	 * multiple tests. The test result class is a decent place for small test setups. But if you're doing more complex
	 * stuff, the classic Utils class is also a good place.
	 */
	@Override
	public boolean compareResults(IdTestResult actual, IdTestResult expected)
	{
		return IdTestResult.compare(actual, expected, testName);
	}

	/**
	 * This method is used in the main test case of the superclass to process the model and produce some kind of desired
	 * result. Most plug-ins already have some kind of output so the easiest thing to do is to just write a conversion
	 * method between the output of your plug-in and the new result type that you've created.<br>
	 * <br>
	 * Our example is just the ID function so it doesn't do anything to the AST. But we do have a conversion method. For
	 * practicality and reuse, the conversion method is placed in the result class so it can be used in multiple tests
	 * that use {@link IdTestResult}.
	 */
	@Override
	public IdTestResult processModel(List<INode> ast)
	{
		IdTestResult actual = IdTestResult.convert(ast);
		return actual;

	}

	/**
	 * Here we return the type of our test result, to help the JSON deserialization. In general, you can always use this
	 * exact code and just place your result in the Type Token. <br>
	 * <br>
	 * The superclass system actually tries to work this type out on its own from the type parameter but it often fails
	 * since this is done with sophisticated reflection trickery. In case it does fail, we can simply override this
	 * method to help it out.
	 */
	@Override
	public Type getResultType()
	{
		Type resultType = new TypeToken<IdTestResult>()
		{
		}.getType();
		return resultType;
	}

	/**
	 * If we wish to update the test results, this is done through Java Properties (passed to the VM as arguments with
	 * <code>-D</code>. So, we must define a specific property for this set of tests that we can then use to update
	 * results. <br>
	 * <br>
	 * If you pass the property as argument, you will update all results for this test. If you want to upgrade a single
	 * result, just prepend the test name to the submitted property: <code>-Dtests.update.example.ID.ex1.vdmsl</code>.
	 * The easiest way to get the name is to simply report it in test failures (this is already done should the test
	 * fail on Parse/TC errors).
	 */
	@Override
	protected String getUpdatePropertyString()
	{
		return UPDATE_PROPERTY;
	}

}
