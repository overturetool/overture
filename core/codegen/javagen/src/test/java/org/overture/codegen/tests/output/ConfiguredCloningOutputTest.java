package org.overture.codegen.tests.output;

import java.io.File;
import java.util.Collection;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.overture.codegen.tests.output.base.JavaOutputTestBase;
import org.overture.codegen.tests.output.util.OutputTestUtil;
import org.overture.codegen.vdm2java.JavaSettings;
import org.overture.config.Release;
import org.overture.config.Settings;
import org.overture.core.testing.PathsProvider;

@RunWith(Parameterized.class)
public class ConfiguredCloningOutputTest extends JavaOutputTestBase
{
	public static final String ROOT = "src" + File.separatorChar + "test"
			+ File.separatorChar + "resources" + File.separatorChar
			+ "cloning_specs";

	public ConfiguredCloningOutputTest(String nameParameter,
			String inputParameter, String resultParameter)
	{
		super(nameParameter, inputParameter, resultParameter);
	}

	@Before
	public void init()
	{
		super.init();
		Settings.release = Release.CLASSIC;
	}

	@Override
	public JavaSettings getJavaSettings()
	{
		JavaSettings javaSettings = new JavaSettings();
		javaSettings.setDisableCloning(true);

		return javaSettings;
	}

	@Parameters(name = "{index} : {0}")
	public static Collection<Object[]> testData()
	{
		return PathsProvider.computePaths(ROOT);
	}

	@Override
	protected String getUpdatePropertyString()
	{
		return OutputTestUtil.UPDATE_PROPERTY_PREFIX + "configuredcloning";
	}
}
