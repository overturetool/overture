package org.overture.codegen.tests;

import java.io.File;

import org.overture.codegen.vdm2java.JavaSettings;

public class PackageTestCase extends SpecificationTestCase
{
	public PackageTestCase()
	{
		
	}
	
	public PackageTestCase(File file)
	{
		super(file);
	}
	
	@Override
	public JavaSettings getJavaSettings()
	{
		JavaSettings javaSettings = super.getJavaSettings();
		javaSettings.setJavaRootPackage("my.model");
		
		return javaSettings;
	}
}
