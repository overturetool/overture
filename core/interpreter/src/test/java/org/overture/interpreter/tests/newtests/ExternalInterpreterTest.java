package org.overture.interpreter.tests.newtests;

import java.lang.reflect.Type;

import org.junit.Ignore;
import org.overture.core.testing.ParamExternalsTest;

import com.google.gson.reflect.TypeToken;

@Ignore
public class ExternalInterpreterTest extends
		ParamExternalsTest<StringInterpreterResult> {

	private static String TEST_UPDATE_PROPERTY = "tests.update.interpreter.external";

	public ExternalInterpreterTest(String nameParameter, String testParameter,
			String resultParameter) {
		super(nameParameter, testParameter, resultParameter);
	}

	@Override
	public StringInterpreterResult processSource() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Type getResultType() {
		Type resultType = new TypeToken<StringInterpreterResult>() {
		}.getType();
		return resultType;

	}

	@Override
	protected String getUpdatePropertyString() {
		return TEST_UPDATE_PROPERTY;
	}

	@Override
	public void compareResults(StringInterpreterResult actual,
			StringInterpreterResult expected) {
		// TODO Auto-generated method stub

	}

}
