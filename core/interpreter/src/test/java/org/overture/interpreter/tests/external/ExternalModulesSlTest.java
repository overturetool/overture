package org.overture.interpreter.tests.external;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.overture.ast.lex.Dialect;
import org.overture.config.Release;
import org.overture.config.Settings;

@RunWith(value = Parameterized.class)
public class ExternalModulesSlTest extends AbstractExternalTest
{

	Set<String> classicSpecifications = new HashSet<>(Arrays.asList(new String[]{
			"recmodify-06.vdm", "recmodify-08.vdm", "recordexpr-05.vdm",
			"recordexpr-06.vdm", "recordexpr-07.vdm", "recordexpr-08.vdm",
			"tupleselect-03.vdm", "functions-12.vdm", "functions-19.vdm",
			"patmat-17.vdm", "assignstmt-21.vdm", "assignstmt-22.vdm",
			"casesstmt-04.vdm", "casesstmt-05.vdm", "exception-09.vdm",
			"returnstmt-06.vdm", "assignstmt-05.vdm", "types-04.vdm",
			"types-06.vdm", "types-10.vdm", "fcttypeinst-01.vdm",
			"recordexpr-01.vdm", "recordexpr-02.vdm", "fctcall-mods-01.vdm",
			"exception-01.vdm", "recmodify-06.vdm", "recmodify-08.vdm",
			"recordexpr-05.vdm", "recordexpr-06.vdm", "recordexpr-07.vdm",
			"recordexpr-08.vdm", "tupleselect-03.vdm", "functions-12.vdm",
			"functions-19.vdm", "patmat-17.vdm", "assignstmt-21.vdm",
			"assignstmt-22.vdm", "casesstmt-04.vdm", "casesstmt-05.vdm",
			"exception-09.vdm", "returnstmt-06.vdm", "assignstmt-05.vdm",
			"types-04.vdm", "types-06.vdm", "types-10.vdm",
			"fcttypeinst-01.vdm", "recordexpr-01.vdm", "recordexpr-02.vdm",
			"fctcall-mods-01.vdm", "exception-01.vdm", "exception-13.vdm"}));

	public ExternalModulesSlTest(Dialect dialect, String suiteName,
			File testSuiteRoot, File file, String storeLocationPart)
	{
		super(dialect, suiteName, testSuiteRoot, file, storeLocationPart);
	}

	@Parameters(name = "{1}")
	public static Collection<Object[]> getData()
	{
		return getData("Interpreter_SL_Modules_TestSuite_External", "cgip/sltest", Dialect.VDM_SL, "vdm");
	}

	@Override
	protected String getPropertyId()
	{
		return "external.module.sl";
	}

	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		if (classicSpecifications.contains(file.getName()))
		{
			Settings.release = Release.CLASSIC;
		}
	}
}
