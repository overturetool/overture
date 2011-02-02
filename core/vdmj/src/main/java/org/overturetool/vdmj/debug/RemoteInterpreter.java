/*******************************************************************************
 *
 *	Copyright (c) 2009 Fujitsu Services Ltd.
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

package org.overturetool.vdmj.debug;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.overturetool.vdmj.definitions.ClassDefinition;
import org.overturetool.vdmj.modules.Module;
import org.overturetool.vdmj.runtime.ClassInterpreter;
import org.overturetool.vdmj.runtime.Interpreter;
import org.overturetool.vdmj.runtime.ModuleInterpreter;
import org.overturetool.vdmj.runtime.SourceFile;
import org.overturetool.vdmj.values.Value;

public class RemoteInterpreter
{
	private final Interpreter interpreter;
	private final DBGPReader dbgp;

	public RemoteInterpreter(Interpreter interpreter, DBGPReader dbgp)
	{
		this.interpreter = interpreter;
		this.dbgp = dbgp;
	}

	public Interpreter getInterpreter()
	{
		return interpreter;
	}

	public DBGPReader getDebugReader()
	{
		return dbgp;
	}

	public String execute(String line) throws Exception
	{
		return interpreter.execute(line, dbgp).toString();
	}

	public Value valueExecute(String line) throws Exception
	{
		return interpreter.execute(line, dbgp);
	}

	public void init()
	{
		interpreter.init(dbgp);
	}

	public void create(String var, String exp) throws Exception
	{
		if (interpreter instanceof ClassInterpreter)
		{
			ClassInterpreter ci = (ClassInterpreter)interpreter;
			ci.create(var, exp);
		}
		else
		{
			throw new Exception("Only available for VDM++ and VDM-RT");
		}
	}

	public String getEnvironment()
	{
		return interpreter.getInitialContext();
	}

	public Set<File> getSourceFiles()
	{
		return interpreter.getSourceFiles();
	}

	public SourceFile getSourceFile(File file) throws IOException
	{
		return interpreter.getSourceFile(file);
	}

	public List<String> getModules() throws Exception
	{
		List<String> names = new Vector<String>();

		if (interpreter instanceof ClassInterpreter)
		{
			throw new Exception("Only available for VDM-SL");
		}
		else
		{
			for (Module m: ((ModuleInterpreter)interpreter).getModules())
			{
				names.add(m.name.name);
			}
		}

		return names;
	}

	public List<String> getClasses() throws Exception
	{
		List<String> names = new Vector<String>();

		if (interpreter instanceof ClassInterpreter)
		{
			for (ClassDefinition def: ((ClassInterpreter)interpreter).getClasses())
			{
				names.add(def.name.name);
			}
		}
		else
		{
			throw new Exception("Only available for VDM++ and VDM-RT");
		}

		return names;
	}
}
