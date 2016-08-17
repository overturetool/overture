package org.overture.codegen.tests.output;

import java.io.File;
import java.util.Collection;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.overture.ast.lex.Dialect;
import org.overture.codegen.tests.output.base.JavaOutputTestBase;
import org.overture.codegen.tests.output.util.OutputTestUtil;
import org.overture.codegen.vdm2java.JavaSettings;
import org.overture.config.Release;
import org.overture.config.Settings;
import org.overture.core.testing.PathsProvider;

@RunWith(Parameterized.class)
public class SystemClassOutputTest extends JavaOutputTestBase
{
	public static final String ROOT = "src" + File.separatorChar + "test"
			+ File.separatorChar + "resources" + File.separatorChar
			+ "system_class_specs";

	public SystemClassOutputTest(String nameParameter, String inputParameter,
			String resultParameter)
	{
		super(nameParameter, inputParameter, resultParameter);
	}

	@Before
	public void init()
	{
		super.init();

		Settings.release = Release.VDM_10;
		Settings.dialect = Dialect.VDM_RT;
	}

	@Override
	public JavaSettings getJavaSettings()
	{
		JavaSettings javaSettings = super.getJavaSettings();
		javaSettings.setGenSystemClass(true);

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
		return OutputTestUtil.UPDATE_PROPERTY_PREFIX + "system";
	}
}
