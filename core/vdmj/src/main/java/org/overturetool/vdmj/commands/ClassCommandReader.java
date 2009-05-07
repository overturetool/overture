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

package org.overturetool.vdmj.commands;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.overturetool.vdmj.definitions.ClassDefinition;
import org.overturetool.vdmj.definitions.ClassList;
import org.overturetool.vdmj.runtime.ClassInterpreter;
import org.overturetool.vdmj.runtime.VDMThreadSet;


/**
 * A class to read and perform class related commands from standard input.
 */

public class ClassCommandReader extends CommandReader
{
	/** A ClassInterpreter version of the interpreter field. */
	private final ClassInterpreter cinterpreter;

	public ClassCommandReader(ClassInterpreter interpreter, String prompt)
	{
		super(interpreter, prompt);
		cinterpreter = interpreter;
	}

	@Override
	protected boolean doDefault(String line) throws Exception
	{
		String parts[] = line.split("\\s+");

		if (parts.length != 2)
		{
			throw new Exception("Usage: default <default class name>");
		}

		cinterpreter.setDefaultName(parts[1]);
		println("Default class set to " + cinterpreter.getDefaultName());
		return true;
	}

	@Override
	protected boolean doClasses(String line)
	{
		String def = cinterpreter.getDefaultName();
		ClassList classes = cinterpreter.getClasses();

		for (ClassDefinition c: classes)
		{
			if (c.name.name.equals(def))
			{
				println(c.name.name + " (default)");
			}
			else
			{
				println(c.name.name);
			}
		}

		return true;
	}

	@Override
	protected boolean doCreate(String line) throws Exception
	{
		Pattern p = Pattern.compile("^create (\\w+)\\s*?:=\\s*(.+)$");
		Matcher m = p.matcher(line);

		if (m.matches())
		{
			String var = m.group(1);
			String exp = m.group(2);

			cinterpreter.create(var, exp);
		}
		else
		{
			throw new Exception("Usage: create <id> := <value>");
		}

		return true;
	}

	@Override
	protected boolean doThreads(String line)
	{
		print(VDMThreadSet.getStatus());
		return true;
	}

	@Override
	protected void doHelp(String line)
	{
		println("classes - list the loaded class names");
		println("threads - list active threads");
		println("default <class> - set the default class name");
		println("create <id> := <exp> - create a named variable");
		super.doHelp(line);
	}
}
