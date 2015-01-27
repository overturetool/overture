package org.overture.codegen.tests;

import java.io.File;

import org.overture.codegen.ir.IRSettings;

public class TracesExpansionTestCase extends SpecificationTestCase
{
	public TracesExpansionTestCase()
	{
		super();
	}

	public TracesExpansionTestCase(File file)
	{
		super(file);
	}
	
	@Override
	public IRSettings getIrSettings()
	{
		IRSettings irSettings = super.getIrSettings();
		irSettings.setGenerateTraces(true);
		
		return irSettings;
	}
}
