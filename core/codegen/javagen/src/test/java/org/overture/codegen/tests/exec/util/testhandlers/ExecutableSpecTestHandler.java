/*
 * #%~
 * VDM Code Generator
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
package org.overture.codegen.tests.exec.util.testhandlers;

import java.io.File;
import java.io.IOException;

import org.overture.ast.lex.Dialect;
import org.overture.codegen.tests.exec.util.ExecutionResult;
import org.overture.config.Release;
import org.overture.config.Settings;
import org.overture.interpreter.util.InterpreterUtil;
import org.overture.interpreter.values.Value;

public class ExecutableSpecTestHandler extends EntryBasedTestHandler
{
	public ExecutableSpecTestHandler(Release release, Dialect dialect)
	{
		super(release, dialect);
	}

	public void writeMainClass(File parent, String rootPackage)
			throws IOException
	{
		injectArgIntoMainClassFile(parent, rootPackage != null ? (rootPackage  + "." + getJavaEntry()) : getJavaEntry());
	}
	
	@Override
	public String getJavaEntry()
	{
		return "Entry.Run()";
	}

	@Override
	public String getVdmEntry()
	{
		return "Entry`Run()";
	}

	@Override
	public ExecutionResult interpretVdm(File intputFile) throws Exception
	{
		Value val = InterpreterUtil.interpret(Settings.dialect, getVdmEntry(), intputFile);
		return new ExecutionResult(val.toString(), val);
	}
}
