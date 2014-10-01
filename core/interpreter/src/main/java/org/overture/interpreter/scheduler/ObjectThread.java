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

import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.lex.LexLocation;
import org.overture.config.Settings;
import org.overture.interpreter.commands.DebuggerReader;
import org.overture.interpreter.debug.DBGPReader;
import org.overture.interpreter.debug.DBGPReason;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.ContextException;
import org.overture.interpreter.runtime.ObjectContext;
import org.overture.interpreter.runtime.ValueException;
import org.overture.interpreter.values.CPUValue;
import org.overture.interpreter.values.ObjectValue;
import org.overture.interpreter.values.OperationValue;
import org.overture.interpreter.values.TransactionValue;
import org.overture.interpreter.values.ValueList;

/**
 * A class representing a VDM thread running in an object.
 */

public class ObjectThread extends SchedulablePoolThread
{
	private static final long serialVersionUID = 1L;
	public final OperationValue operation;
	public final Context ctxt;
	public final String title;
	public final boolean breakAtStart;

	public ObjectThread(ILexLocation location, ObjectValue object, Context ctxt)
			throws ValueException
	{
		super(object.getCPU().resource, object, 0, false, 0);

		setName("ObjectThread-" + getId());

		this.title = "Thread " + getId() + ", self #" + object.objectReference
				+ ", class " + object.type.getName().getName();

		this.ctxt = new ObjectContext(ctxt.assistantFactory, location, title, ctxt.getGlobal(), object);
		this.operation = object.getThreadOperation(ctxt);
		this.breakAtStart = ctxt.threadState.isStepping();
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
			ctxt.setThreadState(null, operation.getCPU());

			if (breakAtStart)
			{
				// Step at the first location you check (start of body)
				ctxt.threadState.setBreaks(new LexLocation(), null, null);
			}

			operation.eval(ctxt.location, new ValueList(), ctxt);// FIXME: use visitor here
		} catch (ValueException e)
		{
			suspendOthers();
			ResourceScheduler.setException(e);
			DebuggerReader.stopped(e.ctxt, operation.name.getLocation());
		} catch (ContextException e)
		{
			suspendOthers();
			ResourceScheduler.setException(e);
			DebuggerReader.stopped(e.ctxt, e.location);
		} catch (Exception e)
		{
			ResourceScheduler.setException(e);
			BasicSchedulableThread.signalAll(Signal.SUSPEND);
		} finally
		{
			TransactionValue.commitAll();
		}
	}

	private void runDBGP()
	{
		DBGPReader reader = null;

		try
		{
			CPUValue cpu = operation.getCPU();
			reader = ctxt.threadState.dbgp.newThread(cpu);
			ctxt.setThreadState(reader, cpu);

			if (breakAtStart)
			{
				// Step at the first location you check (start of body)
				ctxt.threadState.setBreaks(new LexLocation(), null, null);
			}

			operation.eval(ctxt.location, new ValueList(), ctxt);// FIXME: use visitor here

			reader.complete(DBGPReason.OK, null);
		} catch (ContextException e)
		{

			ResourceScheduler.setException(e);
			ctxt.threadState.dbgp.setErrorState();
			// suspendOthers();
			setExceptionOthers();
			reader.stopped(e.ctxt, e.location);
		} catch (Exception e)
		{
			ResourceScheduler.setException(e);
			ctxt.threadState.dbgp.setErrorState();
			BasicSchedulableThread.signalAll(Signal.ERROR);
		} catch (ThreadDeath e)
		{
			reader.complete(DBGPReason.ABORTED, null);
			throw e;
		} finally
		{
			TransactionValue.commitAll();
		}
	}
}
