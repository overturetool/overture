package org.overture.core.tests;

import java.io.Serializable;
import java.util.Collection;
import java.util.Vector;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Test with external inputs. This test behaves exactly like {@link ParamStandardTest}. The only difference is that the
 * test data provider is predefined and works with external inputs specified by passing
 * <code>-DexternalTestsPath=/path/to/files</code> to the VM.<br>
 * <br>
 * Test result files are stored locally for each module under <code>src/test/resources/external</code> *
 * 
 * @author ldc
 * @param <R>
 *            the type of result this test operates on.
 */
@RunWith(Parameterized.class)
public abstract class ParamExternalsTest<R extends Serializable> extends
		ParamStandardTest<R>
{

	private static String EXTERNAL_TESTS_PROPERTY = "externalTestsPath";

	/**
	 * Constructor simply passes the parameters along to the superclass. Since the parameters are provided by
	 * {@link #testData()}, any subclass of this test must have same constructor parameters or will it will have to
	 * declare a new provider.
	 * 
	 * @param nameParameter
	 * @param testParameter
	 * @param resultParameter
	 */
	public ParamExternalsTest(String nameParameter, String testParameter,
			String resultParameter)
	{
		super(nameParameter, testParameter, resultParameter);
	}

	/**
	 * The data provider for this test. The input paths are calculated based off the external path provided. The results
	 * are constructed using the entire path of the input so that the result folder structure mirrors that of the
	 * external inputs.
	 * 
	 * @return a collection of test input and result paths in the form of {filename ,filepath, resultpath} arrays
	 */
	@Parameters(name = "{index} : {0}")
	public static Collection<Object[]> testData()
	{
		String external = System.getProperty(EXTERNAL_TESTS_PROPERTY);

		if (external == null)
		{
			return new Vector<Object[]>();
		} else
		{
			return PathsProvider.computeExternalPaths(external);
		}
	}

}
