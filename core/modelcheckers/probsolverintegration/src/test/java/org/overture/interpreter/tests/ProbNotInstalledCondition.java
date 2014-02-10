package org.overture.interpreter.tests;

import java.io.File;

import org.overture.test.framework.ConditionalIgnoreMethodRule.IgnoreCondition;

public class ProbNotInstalledCondition implements IgnoreCondition
{

	@Override
	public boolean isIgnored()
	{
		String os = System.getProperty("os.name").toLowerCase();

		String cli = null;

		if (os.indexOf("win") >= 0)
		{
			cli = "probcli.exe";
		} else if (os.indexOf("mac") >= 0 || os.indexOf("linux") >= 0)
		{
			cli = "probcli.sh";
		} else
		{
			return true;
		}

		String path = System.getProperty("prob.home");
		if (path == null)
		{
			path = System.getProperty("user.home") + File.separatorChar
					+ ".prob";
		}

		return !new File(path, cli).exists();

	}

}
