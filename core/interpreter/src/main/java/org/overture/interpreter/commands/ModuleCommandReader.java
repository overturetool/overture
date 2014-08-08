/*******************************************************************************
 *
 *	Copyright (C) 2008 Fujitsu Services Ltd.
 *
 *	Author: Nick Battle
 *
 *	This file is part of VDMJ.
 *
 *	VDMJ is free software: you can redistribute it and/or modify
 *	it under the terms of the GNU General Public License as published by
 *	the Free Software Foundation, either version 3 of the License, or
 *	(at your option) any later version.
 *
 *	VDMJ is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with VDMJ.  If not, see <http://www.gnu.org/licenses/>.
 *
 ******************************************************************************/

package org.overture.interpreter.commands;

import java.util.List;

import org.overture.ast.modules.AModuleModules;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.ModuleInterpreter;

/**
 * A class to read and perform module related commands from standard input.
 */

public class ModuleCommandReader extends CommandReader
{
	/** A ModuleInterpreter version of the interpreter field. */
	private final ModuleInterpreter minterpreter;

	public ModuleCommandReader(ModuleInterpreter interpreter, String prompt)
	{
		super(interpreter, prompt);
		minterpreter = interpreter;
	}

	public ModuleCommandReader(ModuleInterpreter interpreter, String prompt,
			boolean singlePass)
	{
		super(interpreter, prompt, singlePass);
		minterpreter = interpreter;
	}

	@Override
	protected boolean doModules(String line)
	{
		String def = minterpreter.getDefaultName();
		List<AModuleModules> modules = minterpreter.getModules();

		for (AModuleModules m : modules)
		{
			if (m.getName().getName().equals(def))
			{
				println(m.getName().getName() + " (default)");
			} else
			{
				println(m.getName().getName());
			}
		}

		return true;
	}

	@Override
	protected boolean doState(String line)
	{
		Context c = minterpreter.getStateContext();
		print(c == null ? "(no state)\n" : c.toString());
		return true;
	}

	@Override
	protected boolean doDefault(String line) throws Exception
	{
		String parts[] = line.split("\\s+");

		if (parts.length != 2)
		{
			throw new Exception("Usage: default <default module name>");
		}

		minterpreter.setDefaultName(parts[1]);
		println("Default module set to " + minterpreter.getDefaultName());
		return true;
	}

	@Override
	protected void doHelp(String line)
	{
		println("modules - list the loaded module names");
		println("default <module> - set the default module name");
		println("state - show the default module state");
		super.doHelp(line);
	}
}
