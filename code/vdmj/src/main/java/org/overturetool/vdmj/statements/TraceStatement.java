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

package org.overturetool.vdmj.statements;

import org.overturetool.vdmj.definitions.NamedTraceDefinition;
import org.overturetool.vdmj.messages.Console;
import org.overturetool.vdmj.runtime.ClassInterpreter;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.Interpreter;
import org.overturetool.vdmj.traces.CallSequence;
import org.overturetool.vdmj.traces.TestSequence;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.values.Value;
import org.overturetool.vdmj.values.VoidValue;

public class TraceStatement extends Statement
{
	public final NamedTraceDefinition tracedef;

	public TraceStatement(NamedTraceDefinition def)
	{
		super(def.location);
		this.tracedef = def;
	}

	@Override
	public Value eval(Context ctxt)
	{
		TestSequence tests = tracedef.getTests(ctxt);
		ClassInterpreter ci = (ClassInterpreter)Interpreter.getInstance();
		int n = 1;

		for (CallSequence test: tests)
		{
			ci.init();	// Initialize completely between every run...

			Console.out.println("Test " + n + " = " + test);
			Console.out.println("Result = " + ci.runtrace(location.module, test));
			n++;
		}

		return new VoidValue();
	}

	@Override
	public String kind()
	{
		return "trace";
	}

	@Override
	public String toString()
	{
		return tracedef.toString();
	}

	@Override
	public Type typeCheck(Environment env, NameScope scope)
	{
		assert false : "Shouldn't be calling TraceStatement's typeCheck";
		return null;
	}
}
