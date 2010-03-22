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

import org.overturetool.vdmj.definitions.NamedTraceDefinition;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.messages.Console;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.ContextException;
import org.overturetool.vdmj.runtime.Interpreter;
import org.overturetool.vdmj.runtime.ValueException;
import org.overturetool.vdmj.traces.CallSequence;
import org.overturetool.vdmj.traces.TestSequence;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.VoidType;
import org.overturetool.vdmj.values.Value;
import org.overturetool.vdmj.values.VoidValue;

public class TraceStatement extends Statement
{
	private static final long serialVersionUID = 1L;
	private static PrintWriter writer = null;
	public final NamedTraceDefinition tracedef;
	private LexNameToken arg;

	public TraceStatement(NamedTraceDefinition def)
	{
		super(def.location);
		this.tracedef = def;

		if (writer == null)
		{
			writer = Console.out;
		}

		arg = new LexNameToken(tracedef.name.module, "_test_", location);
	}

	@Override
	public Value eval(Context ctxt)
	{
		breakpoint.check(location, ctxt);

		TestSequence tests = tracedef.getTests(ctxt);
		Interpreter interpreter = Interpreter.getInstance();
		Value argval = ctxt.check(arg);

		if (argval == null)
		{
    		int n = 1;

    		for (CallSequence test: tests)
    		{
    			// Bodge until we figure out how to not have explicit op names.
    			String clean = test.toString().replaceAll("\\.\\w+`", ".");

    			if (test.getFilter() > 0)
    			{
        			writer.println("Test " + n + " = " + clean);
    				writer.println(
    					"Test " + n + " FILTERED by test " + test.getFilter());
    			}
    			else
    			{
    				// Initialize completely between every run...
        			interpreter.traceInit();
        			List<Object> result = interpreter.runtrace(tracedef.classDefinition, test);
        			tests.filter(result, test, n);

        			writer.println("Test " + n + " = " + clean);
        			writer.println("Result = " + result);
    			}

    			n++;
    		}
		}
		else
		{
			long n = 0;

			try
			{
				n = argval.nat1Value(ctxt);
			}
			catch (ValueException e)
			{
				throw new ContextException(e, location);
			}

			if (n > tests.size())		// Arg is 1 to n
			{
				abort(4143, "No such test number: " + n, ctxt);
			}

			CallSequence test = tests.get((int)(n - 1));
			// Bodge until we figure out how to not have explicit op names.
			String clean = test.toString().replaceAll("\\.\\w+`", ".");

			if (test.getFilter() > 0)
			{
    			writer.println("Test " + n + " = " + clean);
				writer.println(
					"Test " + n + " FILTERED by test " + test.getFilter());
			}
			else
			{
				// Initialize completely between every run...
    			interpreter.traceInit();
    			List<Object> result = interpreter.runtrace(tracedef.classDefinition, test);

    			writer.println("Test " + n + " = " + clean);
    			writer.println("Result = " + result);
			}
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

		env.findName(arg, scope);	// Just to avoid a "not used" warning
		return new VoidType(location);
	}

	public static void setOutput(PrintWriter pw)
	{
		writer = pw;
	}
}
