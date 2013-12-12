package org.overture.interpreter.tests.external;

import java.io.File;
import java.util.Collection;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.overture.ast.lex.Dialect;

@RunWith(value = Parameterized.class)
public class ExternalModulesSlTest extends AbstractExternalTest
{

	public ExternalModulesSlTest(Dialect dialect, String suiteName,
			File testSuiteRoot, File file, String storeLocationPart)
	{
		super(dialect, suiteName, testSuiteRoot, file, storeLocationPart);
	}

	@Parameters(name = "{1}")
	public static Collection<Object[]> getData()
	{
		return getData("Interpreter_SL_Modules_TestSuite_External", "sltest/cgip", Dialect.VDM_SL, "vdm");
	}

	@Override
	protected String getPropertyId()
	{
		return "external.module.sl";
	}
}
