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
public class ExternalClassesPpTest extends AbstractExternalTest

{
	Set<String> classicSpecifications = new HashSet<>(Arrays.asList(new String[]{
			"instvars-01.vpp", "type-02.vpp", "type-03.vpp", "type-04.vpp",
			"depend-04.vpp", "reperr-01.vpp", "reperr-19.vpp", "reperr-21.vpp",
			"reperr-22.vpp", "reperr-23.vpp", "reperr-24.vpp", "reperr-25.vpp",
			"reperr-31.vpp", "reperr-35.vpp", "reperr-36.vpp", "reperr-37.vpp",
			"reperr-38.vpp", "reperr-39.vpp", "instvars-16.vpp",
			"instvars-17.vpp", "isofbaseclass-03.vpp", "isofclass-03.vpp",
			"new-07.vpp", "rep-01.vpp", "rep-03.vpp", "rep-04.vpp",
			"rep-06.vpp", "rep-08.vpp", "rep-09.vpp", "rep-11.vpp",
			"rep-13.vpp", "rep-14.vpp", "rep-16.vpp", "rep-51.vpp",
			"rep-59.vpp", "full-01.vpp", "full-04.vpp", "full-06.vpp",
			"full-08.vpp", "full-14.vpp", "full-15.vpp", "overload-02.vpp",
			"functions-01.vpp", "functions-02.vpp", "functions-05.vpp",
			"functions-07.vpp", "functions-08.vpp", "functions-09.vpp",
			"functions-10.vpp", "functions-11.vpp", "functions-12.vpp",
			"functions-15.vpp", "functions-16.vpp", "functions-17.vpp",
			"functions-19.vpp", "functions-20.vpp", "functions-23.vpp",
			"functions-24.vpp", "functions-25.vpp", "functions-27.vpp",
			"instance-01.vpp", "instance-03.vpp", "instance-04.vpp",
			"instance-05.vpp", "instance-06.vpp", "instance-09.vpp",
			"instance-11.vpp", "instance-12.vpp", "instance-13.vpp",
			"instance-14.vpp", "instance-16.vpp", "operations-05.vpp",
			"operations-08.vpp", "operations-09.vpp", "operations-10.vpp",
			"operations-12.vpp", "operations-14.vpp", "operations-15.vpp",
			"polyfun-02.vpp", "polyfun-05.vpp", "polyfun-07.vpp",
			"polyfun-08.vpp", "polyfun-09.vpp", "polyfun-10.vpp",
			"polyfun-11.vpp", "polyfun-12.vpp", "polyfun-15.vpp",
			"polyfun-16.vpp", "polyfun-17.vpp", "polyfun-19.vpp",
			"polyfun-20.vpp", "polyfun-23.vpp", "polyfun-24.vpp",
			"polyfun-25.vpp", "polyfun-27.vpp", "types-01.vpp", "types-05.vpp",
			"types-06.vpp", "types-08.vpp", "types-13.vpp", "types-14.vpp",
			"types-15.vpp", "types-18.vpp", "types-23.vpp", "types-24.vpp",
			"types-25.vpp", "types-27.vpp", "types-28.vpp", "types-31.vpp",
			"types-32.vpp", "types-33.vpp", "types-35.vpp", "types-38.vpp",
			"types-40.vpp", "types-41.vpp", "types-42.vpp", "types-44.vpp",
			"types-45.vpp", "types-46.vpp", "values-01.vpp", "values-02.vpp",
			"values-03.vpp", "values-05.vpp", "values-06.vpp", "values-13.vpp",
			"values-14.vpp", "invoke-15.vpp"}));

	public ExternalClassesPpTest(Dialect dialect, String suiteName,
			File testSuiteRoot, File file, String storeLocationPart)
	{
		super(dialect, suiteName, testSuiteRoot, file, storeLocationPart);
	}

	@Parameters(name = "{1}")
	public static Collection<Object[]> getData()
	{
		return getData("Interpreter_PP_Classes_TestSuite_External", "cgip/pptest", Dialect.VDM_PP, "vpp");
	}

	@Override
	protected String getPropertyId()
	{
		return "external.class.pp";
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
