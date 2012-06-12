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

import org.overture.ast.expressions.PExp;
import org.overture.config.Settings;
import org.overture.interpreter.commands.DebuggerReader;
import org.overture.interpreter.messages.Console;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.ContextException;
import org.overture.interpreter.runtime.ThreadState;
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
	private Exception exception = null;

	public MainThread(PExp expr, Context ctxt)
	{
		super(CPUResource.vCPU, null, 0, false, 0);

		this.expression = expr;
		this.ctxt = ctxt;
		this.exception = null;

		setName("MainThread-" + getId());
	}

	@Override
	public int hashCode()
	{
		return (int)getId();
	}

	@Override
	public void body()
	{
		if (Settings.usingDBGP)
		{
			runDBGP();
		}
		else
		{
			runCmd();
		}
	}

	private void runCmd()
	{
		try
		{
			result = expression.eval(ctxt);
		}
		catch (ContextException e)
		{
			setException(e);
			suspendOthers();
			DebuggerReader.stopped(e.ctxt, e.location);
		}
		catch (Exception e)
		{
			setException(e);
			suspendOthers();
		}
		finally
		{
			TransactionValue.commitAll();
		}
	}

	private void runDBGP()
	{
		try
		{
			result = expression.eval(ctxt);
		}
		catch (ContextException e)
		{
			//If the exception is raised from the console location the debugger is stopped.
			if (e.location.file.getName().equals(LexTokenReader.consoleFileName))
			{
				ThreadState s = ctxt.threadState;
				s.dbgp.invocationError(e);//TODO
				BasicSchedulableThread.signalAll(Signal.TERMINATE);
			}
			else
			{
				suspendOthers();
				setException(e);
				ctxt.threadState.dbgp.stopped(e.ctxt, e.location);
			}
		}
		catch (Exception e)
		{
			setException(e);
			BasicSchedulableThread.signalAll(Signal.SUSPEND);
		}
		catch(StackOverflowError e)
		{
			ctxt.threadState.dbgp.invocationError(e);
			BasicSchedulableThread.signalAll(Signal.TERMINATE);
		}
		finally
		{
			TransactionValue.commitAll();
		}
	}

	public Value getResult() throws Exception
	{
		if (exception != null)
		{
			throw exception;
		}

		return result;
	}

	public void setException(Exception e)
	{
		Console.err.println(e.getMessage());
		exception = e;

		if (ctxt.threadState.dbgp != null)
		{
			ctxt.threadState.dbgp.setErrorState();
		}
	}
}
