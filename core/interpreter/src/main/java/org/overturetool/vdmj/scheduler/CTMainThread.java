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

package org.overturetool.vdmj.scheduler;

import java.util.List;
import java.util.Vector;

import org.overturetool.vdmj.Settings;
import org.overturetool.vdmj.commands.DebuggerReader;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.ContextException;
import org.overturetool.vdmj.statements.Statement;
import org.overturetool.vdmj.traces.CallSequence;
import org.overturetool.vdmj.traces.TraceVariableStatement;
import org.overturetool.vdmj.traces.Verdict;

/**
 * A class representing the main VDM thread.
 */

public class CTMainThread extends MainThread
{
	private static final long serialVersionUID = 1L;
	private final CallSequence test;
	private final boolean debug;

	private List<Object> result = new Vector<Object>();

	public CTMainThread(CallSequence test, Context ctxt, boolean debug)
	{
		super(null, ctxt);

		this.test = test;
		this.debug = debug;

		setName("CTMainThread-" + getId());
	}

	@Override
	public int hashCode()
	{
		return (int)getId();
	}

	@Override
	public void body()
	{
		try
		{
			for (Statement statement: test)
			{
				if (statement instanceof TraceVariableStatement)
				{
					// Just update the context...
					statement.eval(ctxt);
				}
				else
				{
 					result.add(statement.eval(ctxt));
				}
			}

			result.add(Verdict.PASSED);
		}
		catch (ContextException e)
		{
			result.add(e.getMessage().replaceAll(" \\(.+\\)", ""));

			if (debug)
			{
				setException(e);
				suspendOthers();

				if (Settings.usingDBGP)
				{
					ctxt.threadState.dbgp.stopped(e.ctxt, e.location);
				}
				else
				{
					DebuggerReader.stopped(e.ctxt, e.location);
				}

				result.add(Verdict.FAILED);
			}
			else
			{
				// These exceptions are inconclusive if they occur
				// in a call directly from the test because it could
				// be a test error, but if the test call has made
				// further call(s), then they are real failures.

    			switch (e.number)
    			{
    				case 4055:	// precondition fails for functions
    				case 4071:	// precondition fails for operations

    					if (e.ctxt.outer == ctxt)
    					{
    						result.add(Verdict.INCONCLUSIVE);
    					}
    					else
    					{
    						result.add(Verdict.FAILED);
    					}
    					break;

    				case 4075: case 4076: case 4077:	// invalid type conversions
    				case 4060:							// type invariant failures
    				case 4058: case 4059:
    				case 4064: case 4065:
    				case 4134:							// type conversion failures

    					if (e.ctxt == ctxt)
    					{
    						result.add(Verdict.INCONCLUSIVE);
    					}
    					else
    					{
    						result.add(Verdict.FAILED);
    					}
    					break;

    				default:
    					result.add(Verdict.FAILED);
    			}
			}
		}
		catch (Exception e)
		{
			result.add(e.getMessage());
			result.add(Verdict.FAILED);
		}
	}

	@Override
	protected void handleSignal(Signal sig, Context lctxt, LexLocation location)
	{
		if (sig == Signal.DEADLOCKED)
		{
			result.add("DEADLOCK detected");
			result.add(Verdict.FAILED);
		}

		super.handleSignal(sig, lctxt, location);
	}

	public List<Object> getList()
	{
		return result;
	}
}
