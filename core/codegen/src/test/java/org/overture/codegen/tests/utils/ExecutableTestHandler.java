package org.overture.codegen.tests.utils;

import java.io.File;

import org.overture.config.Release;
import org.overture.config.Settings;
import org.overture.interpreter.values.Value;

public abstract class ExecutableTestHandler extends TestHandler
{
	public ExecutableTestHandler(Release release)
	{
		Settings.release = release;
	}
	
	public abstract Value interpretVdm(File intputFile) throws Exception;
}
