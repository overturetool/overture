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
public class ExternalClassesRtTest extends AbstractExternalTest
{
	Set<String> classicSpecifications = new HashSet<>(Arrays.asList(new String[]{
			"extension-01.vpp", "extension-07.vpp", "extension-12.vpp",
			"extension-13.vpp", "extension-16.vpp", "fighter-01.vpp",
			"fighter-02.vpp", "fighter-03.vpp", "fighter-04.vpp",
			"fighter-05.vpp", "fighter-06.vpp", "fighter-07.vpp",
			"fighter-08.vpp", "periodic-08.vpp", "periodic-09.vpp",
			"staticext-03.vpp", "staticext-05.vpp"}));

	public ExternalClassesRtTest(Dialect dialect, String suiteName,
			File testSuiteRoot, File file, String storeLocationPart)
	{
		super(dialect, suiteName, testSuiteRoot, file, storeLocationPart);
	}

	@Parameters(name = "{1}")
	public static Collection<Object[]> getData()
	{
		return getData("Interpreter_RT_Classes_TestSuite_External", "cgip/rttest", Dialect.VDM_RT, "vpp");
	}

	@Override
	protected String getPropertyId()
	{
		return "external.class.rt";
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
