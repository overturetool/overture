package org.overture.core.tests;

import java.io.Serializable;
import java.util.Collection;
import java.util.Vector;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public abstract class ExternalsTest<R extends Serializable> extends AbsParamBasicTest<R>
{

	private static String EXTERNAL_TESTS_PROPERTY = "externalTestsPath";

	public ExternalsTest(String nameParameter, String testParameter,
			String resultParameter)
	{
		super(nameParameter, testParameter, resultParameter);
	}

	@Parameters(name = "{index} : {0}")
	public static Collection<Object[]> testData()
	{
		String external = System.getProperty(EXTERNAL_TESTS_PROPERTY);
		

		if (external == null)
		{
			return new Vector<Object[]>();
		} else
		{
			return PathsProvider.computePathsLegacy(external);
		}
	}

}
