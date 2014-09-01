/*
 * #%~
 * Integration of the ProB Solver for the VDM Interpreter
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
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
