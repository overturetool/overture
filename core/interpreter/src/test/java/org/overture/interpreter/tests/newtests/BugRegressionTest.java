package org.overture.interpreter.tests.newtests;

import java.util.Collection;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.overture.config.Release;
import org.overture.config.Settings;
import org.overture.core.tests.PathsProvider;

@RunWith(Parameterized.class)
public class BugRegressionTest extends ParamInterpreterTest {

	private final static String BUG_REGRESSION_ROOT = "src/test/resources/regression";
	
	public BugRegressionTest(String nameParameter, String inputParameter,
			String resultParameter) {
		super(nameParameter, inputParameter, resultParameter);
	}
	
	@Parameters(name = "{index} : {0}")
	public static Collection<Object[]> testData()
	{
		return PathsProvider.computePaths(BUG_REGRESSION_ROOT);
	}

	@Before
	public void setUp() throws Exception
	{
		Settings.release = Release.DEFAULT;
	}

}
