package org.overture.codegen.tests;

import java.io.File;

import org.overture.codegen.ooast.OoAstSettings;

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
	public OoAstSettings getSettings()
	{
		OoAstSettings settings = new OoAstSettings();
		settings.setCharSeqAsString(true);
		
		return settings;
	}
}
