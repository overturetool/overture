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

import org.overture.ast.expressions.PExp;
import org.overture.config.Settings;
import org.overture.interpreter.commands.DebuggerReader;
import org.overture.interpreter.messages.Console;
import org.overture.interpreter.runtime.CollectedContextException;
import org.overture.interpreter.runtime.CollectedExceptions;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.ContextException;
import org.overture.interpreter.runtime.VdmRuntime;
import org.overture.interpreter.values.TransactionValue;
import org.overture.interpreter.values.UndefinedValue;
import org.overture.interpreter.values.Value;
import org.overture.parser.lex.LexTokenReader;

/**
 * A class representing the main VDM thread.
 */

public class MainThread extends SchedulablePoolThread
{
	private static final long serialVersionUID = 1L;
	public final Context ctxt;
	public final PExp expression;

	private Value result = new UndefinedValue();
	protected Vector<Exception> exception = new Vector<Exception>();

	public MainThread(PExp expr, Context ctxt)
	{
		super(CPUResource.vCPU, null, 0, false, 0);

		this.expression = expr;
		this.ctxt = ctxt;
		this.exception = new Vector<Exception>();

		setName("MainThread-" + getId());
	}

	@Override
	public int hashCode()
	{
		return (int) getId();
	}

	@Override
	public void body()
	{
		if (Settings.usingDBGP)
		{
			runDBGP();
		} else
		{
			runCmd();
		}
	}

	private void runCmd()
	{
		try
		{
			result = expression.apply(VdmRuntime.getExpressionEvaluator(), ctxt);
		} catch (ContextException e)
		{
			setException(e);
			suspendOthers();
			DebuggerReader.stopped(e.ctxt, e.location);
		} catch (Exception e)
		{
			setException(e);
			suspendOthers();
		} catch (Throwable e)
		{
			if (e instanceof ThreadDeath)
			{
				// ThreadDeath required re-throw by definition
				throw (ThreadDeath) e;
			}
			setException(new Exception("internal error", e));
			suspendOthers();
		} finally
		{
			TransactionValue.commitAll();
		}
	}

	private void runDBGP()
	{
		try
		{
			result = expression.apply(VdmRuntime.getExpressionEvaluator(), ctxt);
		} catch (ContextException e)
		{
			// If the exception is raised from the console location the debugger is stopped.
			if (e.location.getFile().getName().equals(LexTokenReader.consoleFileName))
			{
				setException(e);
				BasicSchedulableThread.signalAll(Signal.TERMINATE);
			} else
			{
				suspendOthers();
				setException(e);
				ctxt.threadState.dbgp.stopped(e.ctxt, e.location);
			}
		} catch (Exception e)
		{
			setException(e);
			BasicSchedulableThread.signalAll(Signal.SUSPEND);
		} catch (StackOverflowError e)
		{
			setException(new Exception("internal error", e));
			BasicSchedulableThread.signalAll(Signal.TERMINATE);
		} catch (ThreadDeath e)
		{
		} catch (Throwable e)

		{
			setException(new Exception("internal error", e));
			BasicSchedulableThread.signalAll(Signal.SUSPEND);
		} finally
		{
			TransactionValue.commitAll();
		}
	}

	public Value getResult() throws Exception
	{
		if (!exception.isEmpty())
		{
			if (exception.firstElement() instanceof ContextException)
			{
				throw new CollectedContextException((ContextException) exception.firstElement(), exception);
			} else
			{
				throw new CollectedExceptions(exception);
			}
		}

		return result;
	}

	public List<Exception> getExceptions()
	{
		return exception;
	}

	public void setException(Exception e)
	{
		Console.err.println(e.getMessage());
		if(e.getCause()!=null)
		{
			if(e.getCause() instanceof Error)
			{
				Console.err.println(e.getCause());
				e.getCause().printStackTrace(Console.err);
			}
			else
			{
				Console.err.println(e.getCause().getMessage());
			}
		}
		exception.add(e);

		if (ctxt.threadState.dbgp != null)
		{
			ctxt.threadState.dbgp.setErrorState();
		}
	}
}
