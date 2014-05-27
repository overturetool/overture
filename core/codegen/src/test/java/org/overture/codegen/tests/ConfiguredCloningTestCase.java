package org.overture.codegen.tests;

import java.io.File;

import org.overture.codegen.vdm2java.JavaSettings;
import org.overture.config.Release;
import org.overture.config.Settings;

public class ConfiguredCloningTestCase extends SpecificationTestCase
{
	public ConfiguredCloningTestCase()
	{
	}
	
	public ConfiguredCloningTestCase(File file)
	{
		super(file);
	}
	
	@Override
	protected void setUp() throws Exception
	{
		Settings.release = Release.CLASSIC;
	}
	
	@Override
	public JavaSettings getJavaSettings()
	{
		JavaSettings javaSettings = new JavaSettings();
		javaSettings.setDisableCloning(true);
		
		return javaSettings;
	}
}
