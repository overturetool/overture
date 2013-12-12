package org.overture.interpreter.tests.external;

import java.io.File;
import java.util.Collection;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.overture.ast.lex.Dialect;

@RunWith(value = Parameterized.class)
public class ExternalClassesRtTest extends AbstractExternalTest
{

	public ExternalClassesRtTest(Dialect dialect, String suiteName,
			File testSuiteRoot, File file, String storeLocationPart)
	{
		super(dialect, suiteName, testSuiteRoot, file, storeLocationPart);
	}

	@Parameters(name = "{1}")
	public static Collection<Object[]> getData()
	{
		return getData("Interpreter_RT_Classes_TestSuite_External", "rttest/cgip", Dialect.VDM_RT, "vpp");
	}

	@Override
	protected String getPropertyId()
	{
		return "external.class.rt";
	}
}
