package org.overture.codegen.tests;

import java.io.File;

import org.overture.codegen.ir.IRSettings;

public class ConfiguredStringGenerationTestCase extends SpecificationTestCase
{
	public ConfiguredStringGenerationTestCase()
	{
	}
	
	public ConfiguredStringGenerationTestCase(File file)
	{
		super(file);
	}
	
	
	@Override
	public IRSettings getIrSettings()
	{
		IRSettings settings = new IRSettings();
		settings.setCharSeqAsString(true);
		
		return settings;
	}
}
