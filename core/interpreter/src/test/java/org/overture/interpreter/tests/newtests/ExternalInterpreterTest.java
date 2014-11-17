package org.overture.interpreter.tests.newtests;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Vector;

import org.junit.runners.Parameterized.Parameters;
import org.overture.core.tests.ParamExternalsTest;
import org.overture.core.tests.PathsProvider;

public class ExternalInterpreterTest extends ParamExternalsTest<StringInterpreterResult>{

	
	private static String EXTERNAL_TESTS_PROPERTY = "externalTestsPath";
	
	public ExternalInterpreterTest(String nameParameter, String testParameter,
			String resultParameter) {
		super(nameParameter, testParameter, resultParameter);
		// TODO Auto-generated constructor stub
	}

	@Parameters
	public static Collection<Object[]> testData()
	{
		String external = System.getProperty(EXTERNAL_TESTS_PROPERTY);

		if (external == null)
		{
			return new Vector<Object[]>();
		} else
		{
			Collection<Object[]> r = PathsProvider.computeExternalPaths(external + "pptest/cgip/");
			r.addAll( PathsProvider.computeExternalPaths(external + "rttest/cgip/"));
			r.addAll( PathsProvider.computeExternalPaths(external + "sltest/cgip/"));
			return r;
		}
	}
	
	@Override
	public StringInterpreterResult processSource() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Type getResultType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getUpdatePropertyString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void compareResults(StringInterpreterResult actual,
			StringInterpreterResult expected) {
		// TODO Auto-generated method stub
		
	}

}
