package org.overture.codegen.tests;

import java.io.File;

import org.overture.codegen.ir.IRSettings;

public class PrePostTestCase extends SpecificationTestCase
{

	public PrePostTestCase()
	{
	}

	public PrePostTestCase(File file)
	{
		super(file);
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
}
