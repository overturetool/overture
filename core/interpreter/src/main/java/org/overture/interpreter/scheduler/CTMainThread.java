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

package org.overture.interpreter.scheduler;

import java.util.List;
import java.util.Vector;
import java.util.ArrayList;

import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.statements.PStm;
import org.overture.config.Settings;
import org.overture.interpreter.commands.DebuggerReader;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.ContextException;
import org.overture.interpreter.runtime.VdmRuntime;
import org.overture.interpreter.traces.CallSequence;
import org.overture.interpreter.traces.TraceVariableStatement;
import org.overture.interpreter.traces.Verdict;

/**
 * A class representing the main VDM thread.
 */

public class CTMainThread extends MainThread
{
	private static final long serialVersionUID = 1L;
	private final CallSequence test;
	private final boolean debug;

	private List<Object> result = new ArrayList<Object>();

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
		return (int) getId();
	}

	@Override
	public void body()
	{
		try
		{
			for (PStm statement : test)
			{
				if (statement instanceof TraceVariableStatement)
				{
					// Just update the context...
					TraceVariableStatement.eval((TraceVariableStatement) statement, ctxt);
				} else
				{
					result.add(statement.apply(VdmRuntime.getStatementEvaluator(), ctxt));
				}
			}

			result.add(Verdict.PASSED);
		} catch (ContextException e)
		{
			result.add(e.getMessage().replaceAll(" \\(.+\\)", ""));

			if (debug)
			{
				setException(e);
				suspendOthers();

				if (Settings.usingDBGP)
				{
					ctxt.threadState.dbgp.stopped(e.ctxt, e.location);
				} else
				{
					DebuggerReader.stopped(e.ctxt, e.location);
				}

				result.add(Verdict.FAILED);
			} else
			{
				// These exceptions are inconclusive if they occur
				// in a call directly from the test because it could
				// be a test error, but if the test call has made
				// further call(s), then they are real failures.

				switch (e.number)
				{
					case 4055: // precondition fails for functions

						if (e.ctxt.outer != null && e.ctxt.outer.outer == ctxt)
						{
							result.add(Verdict.INCONCLUSIVE);
						} else
						{
							result.add(Verdict.FAILED);
						}
						break;

					case 4071: // precondition fails for operations

						if (e.ctxt.outer == ctxt)
						{
							result.add(Verdict.INCONCLUSIVE);
						} else
						{
							result.add(Verdict.FAILED);
						}
						break;
					default:
						if (e.ctxt == ctxt)
						{
							result.add(Verdict.INCONCLUSIVE);
						} else
						{
							result.add(Verdict.FAILED);
						}
						break;
				}
			}
		} catch (Throwable e)
		{
			if (e instanceof ThreadDeath)
			{
				return;
			}
			
			if (result.lastIndexOf(Verdict.FAILED) < 0)
			{
				if (!getExceptions().isEmpty())
				{
					result.addAll(getExceptions());
				} else
				{
					if(e instanceof StackOverflowError)
					{
						result.add("Thread died due to stack overflow");
					}
					else
					{
						result.add(e.getMessage());
					}
				}

				result.add(Verdict.FAILED);
			}
		}
	}

	@Override
	public synchronized void setSignal(Signal sig)
	{
		if (sig == Signal.DEADLOCKED)
		{
			if (result.lastIndexOf(Verdict.FAILED) < 0)
			{
				result.add("DEADLOCK detected");
				result.add(Verdict.FAILED);
			}
		}
		super.setSignal(sig);
	}

	@Override
	protected void handleSignal(Signal sig, Context lctxt, ILexLocation location)
	{
		if (sig == Signal.DEADLOCKED)
		{
			if (result.lastIndexOf(Verdict.FAILED) < 0)
			{
				result.add("DEADLOCK detected");
				result.add(Verdict.FAILED);
			}
		}

		super.handleSignal(sig, lctxt, location);
	}

	public List<Object> getList()
	{
		return result;
	}

	public void setException(Exception e)
	{
		// Don't print out the error for CT
		exception.add(e);
	}
}
