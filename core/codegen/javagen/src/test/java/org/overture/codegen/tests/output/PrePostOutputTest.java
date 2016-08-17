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
public class PrePostOutputTest extends JavaOutputTestBase
{
	public static final String ROOT = "src" + File.separatorChar + "test"
			+ File.separatorChar + "resources" + File.separatorChar
			+ "pre_post_specs";

	public PrePostOutputTest(String nameParameter, String inputParameter,
			String resultParameter)
	{
		super(nameParameter, inputParameter, resultParameter);
	}

	@Override
	public IRSettings getIrSettings()
	{
		IRSettings irSettings = new IRSettings();

		irSettings.setGeneratePreConds(true);
		irSettings.setGeneratePreCondChecks(true);
		irSettings.setGeneratePostConds(true);
		irSettings.setGeneratePostCondChecks(true);

		return irSettings;
	}

	@Parameters(name = "{index} : {0}")
	public static Collection<Object[]> testData()
	{
		return PathsProvider.computePaths(ROOT);
	}

	@Override
	protected String getUpdatePropertyString()
	{
		return OutputTestUtil.UPDATE_PROPERTY_PREFIX + "prepost";
	}
}
