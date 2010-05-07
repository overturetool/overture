/*******************************************************************************
 * Copyright (c) 2009 Fujitsu Services Ltd. Author: Nick Battle This file is part of VDMJ. VDMJ is free software: you
 * can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any later version. VDMJ is distributed in
 * the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a
 * copy of the GNU General Public License along with VDMJ. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package org.overturetool.vdmj.debug;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.overturetool.vdmj.messages.Console;
import org.overturetool.vdmj.runtime.ClassInterpreter;
import org.overturetool.vdmj.runtime.Interpreter;
import org.overturetool.vdmj.values.Value;
import org.overturetool.vdmj.values.VoidValue;

public class RemoteInterpreter
{
	private final Interpreter interpreter;
	private final DBGPReader dbgp;

	public RemoteInterpreter(Interpreter interpreter, DBGPReader dbgp)
	{
		this.interpreter = interpreter;
		this.dbgp = dbgp;
	}

	public String execute(String line) throws Exception
	{
		return valueExecute(line).toString();
	}

	public Value valueExecute(String line) throws Exception
	{
		boolean print = false;
		if(line.startsWith("p ")||line.startsWith("print ")||line.startsWith("debug "))
		{
			line = line.substring(line.indexOf(' '));
			print = true;
		}
		if (interpreter instanceof ClassInterpreter
				&& line.startsWith("create"))
		{
			Pattern p = Pattern.compile("^create (\\w+)\\s*?:=\\s*(.+)$");
			Matcher m = p.matcher(line);

			if (m.matches())
			{
				String var = m.group(1);
				String exp = m.group(2);

				((ClassInterpreter) interpreter).create(var, exp);

			}
			return new VoidValue();

		} else
		{

			Value res = interpreter.execute(line, dbgp);
			if(print && !(res instanceof VoidValue))
			{
				Console.out.println(res.toString());
			}
			return res;
		}
	}

	public void init()
	{
		interpreter.init(null);
	}
}
