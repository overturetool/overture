package org.overture.codegen.tests;

import java.io.File;

public class ConfiguredStringGenerationTestCase extends SpecificationTestCase
{
	public ConfiguredStringGenerationTestCase()
	{
	}
	
	public ConfiguredStringGenerationTestCase(File file)
	{
		super(file);
	}
	
	public boolean generateCharSequencesAsStrings()
	{
		return false;
	};
}
