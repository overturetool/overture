package org.overture.interpreter.tests.newtests;

import java.util.Collection;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.overture.config.Release;
import org.overture.config.Settings;
import org.overture.core.testing.PathsProvider;

@RunWith(Parameterized.class)
public class ClassesPpClassicTest extends ParamInterpreterTest
{
	
	private static final String TEST_UPDATE_PROPERTY = "tests.update.interpreter.ppclassic";
	private final static String CLASSES_CLASSIC_ROOT = "src/test/resources/classesClassic";

	public ClassesPpClassicTest(String nameParameter, String inputParameter,
			String resultParameter)
	{
		super(nameParameter, inputParameter, resultParameter);
	}

	@Parameters(name = "{index} : {0}")
	public static Collection<Object[]> testData()
	{
		return PathsProvider.computePaths(CLASSES_CLASSIC_ROOT);
	}

	@Before
	public void setUp() throws Exception
	{
		Settings.release = Release.CLASSIC;
	}
	
	@Override
	protected String getUpdatePropertyString() {
		return TEST_UPDATE_PROPERTY;
	}

}
