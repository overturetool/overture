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

import java.io.PrintWriter;
import java.util.List;
import java.util.ListIterator;

import org.overturetool.vdmj.definitions.ClassDefinition;
import org.overturetool.vdmj.definitions.NamedTraceDefinition;
import org.overturetool.vdmj.messages.Console;
import org.overturetool.vdmj.runtime.ClassInterpreter;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.Interpreter;
import org.overturetool.vdmj.traces.CallSequence;
import org.overturetool.vdmj.traces.TestSequence;
import org.overturetool.vdmj.traces.Verdict;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.FlatEnvironment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.typechecker.PrivateClassEnvironment;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.values.Value;
import org.overturetool.vdmj.values.VoidValue;

public class TraceStatement extends Statement
{
	private static final long serialVersionUID = 1L;
	private static PrintWriter writer = null;
	public final NamedTraceDefinition tracedef;

	public TraceStatement(NamedTraceDefinition def)
	{
		super(def.location);
		this.tracedef = def;

		if (writer == null)
		{
			writer = Console.out;
		}
	}

	@Override
	public Value eval(Context ctxt)
	{
		TestSequence tests = tracedef.getTests(ctxt);
		ClassInterpreter ci = (ClassInterpreter)Interpreter.getInstance();
		ClassDefinition classdef = tracedef.classDefinition;

		Environment env = new FlatEnvironment(
			classdef.getSelfDefinition(),
			new PrivateClassEnvironment(classdef, ci.getGlobalEnvironment()));

		int n = 1;

		for (CallSequence test: tests)
		{
			if (test.getFilter() > 0)
			{
    			writer.println("Test " + n + " = " + test);
				writer.println(
					"Test " + n + " FILTERED by test " + test.getFilter());
			}
			else
			{
    			ci.init(null);	// Initialize completely between every run...
    			List<Object> result = ci.runtrace(env, test);

    			if (result.get(result.size()-1) == Verdict.FAILED)
    			{
    				String stem = test.toString(result.size() - 1);
    				ListIterator<CallSequence> it = tests.listIterator(n);

    				while (it.hasNext())
    				{
    					CallSequence other = it.next();

    					if (other.toString().startsWith(stem))
    					{
    						other.setFilter(n);
    					}
    				}
    			}

    			// Bodge until we figure out how to not have explicit op names.
    			String clean = test.toString().replaceAll("\\.\\w+`", ".");

    			writer.println("Test " + n + " = " + clean);
    			writer.println("Result = " + result);
			}

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

	public static void setOutput(PrintWriter pw)
	{
		writer = pw;
	}
}
