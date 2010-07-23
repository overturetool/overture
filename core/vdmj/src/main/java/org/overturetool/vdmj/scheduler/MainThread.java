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

import org.overturetool.vdmj.Settings;
import org.overturetool.vdmj.commands.DebuggerReader;
import org.overturetool.vdmj.expressions.Expression;
import org.overturetool.vdmj.messages.Console;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.ContextException;
import org.overturetool.vdmj.values.TransactionValue;
import org.overturetool.vdmj.values.UndefinedValue;
import org.overturetool.vdmj.values.Value;

/**
 * A class representing the main VDM thread.
 */

public class MainThread extends SchedulablePoolThread
{
	private static final long serialVersionUID = 1L;
	public final Context ctxt;
	public final Expression expression;

	private Value result = new UndefinedValue();
	private Exception exception = null;

	public MainThread(Expression expr, Context ctxt)
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
			suspendOthers();
			setException(e);
			ctxt.threadState.dbgp.stopped(e.ctxt, e.location);
		}
		catch (Exception e)
		{
			setException(e);
			BasicSchedulableThread.signalAll(Signal.SUSPEND);
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
	}
}
