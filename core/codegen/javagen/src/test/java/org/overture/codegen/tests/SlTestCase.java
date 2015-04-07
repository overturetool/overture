package org.overture.codegen.tests;

import java.io.File;

import org.overture.ast.lex.Dialect;
import org.overture.config.Release;
import org.overture.config.Settings;

public class SlTestCase extends SpecificationTestCase
{
	public SlTestCase()
	{
	}
	
	public SlTestCase(File file)
	{
		super(file);
	}
	
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		Settings.release = Release.VDM_10;
		Settings.dialect = Dialect.VDM_SL;
	}
}
