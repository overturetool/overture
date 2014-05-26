package org.overture.codegen.tests;

import java.io.File;

import org.overture.config.Release;
import org.overture.config.Settings;

public class ClassicSpecTestCase extends SpecificationTestCase
{
	public ClassicSpecTestCase()
	{
		super();
	}
	
	public ClassicSpecTestCase(File file)
	{
		super(file);
	}
	
	@Override
	protected void setUp() throws Exception
	{
		Settings.release = Release.CLASSIC;
	}
}
