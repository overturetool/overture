package org.overture.codegen.tests.output;

import java.io.File;
import java.util.Collection;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.overture.codegen.ir.IRSettings;
import org.overture.codegen.tests.output.base.JavaOutputTestBase;
import org.overture.codegen.tests.output.util.OutputTestUtil;
import org.overture.core.testing.PathsProvider;

@RunWith(Parameterized.class)
public class ConfiguredStringOutputTest extends JavaOutputTestBase
{
	public ConfiguredStringOutputTest(String nameParameter,
			String inputParameter, String resultParameter)
	{
		super(nameParameter, inputParameter, resultParameter);
	}

	public static final String ROOT = "src" + File.separatorChar + "test"
			+ File.separatorChar + "resources" + File.separatorChar
			+ "string_specs";

	@Override
	public IRSettings getIrSettings()
	{
		IRSettings settings = new IRSettings();
		settings.setCharSeqAsString(true);

		return settings;
	}

	@Parameters(name = "{index} : {0}")
	public static Collection<Object[]> testData()
	{
		return PathsProvider.computePaths(ROOT);
	}

	@Override
	protected String getUpdatePropertyString()
	{
		return OutputTestUtil.UPDATE_PROPERTY_PREFIX + "configuredstring";
	}
}
