package org.overture.codegen.tests;

import java.io.File;

import org.overture.codegen.ir.IRSettings;

public class TracesVerdictTestCase extends TracesExpansionTestCase
{
	public TracesVerdictTestCase()
	{
		super();
	}

	public TracesVerdictTestCase(File file)
	{
		super(file);
	}
	
	@Override
	public IRSettings getIrSettings()
	{
		IRSettings irSettings = super.getIrSettings();
		irSettings.setGenerateTraces(true);
		irSettings.setGeneratePreCondChecks(true);
		irSettings.setGeneratePreConds(true);
		
		return irSettings;
	}
}
