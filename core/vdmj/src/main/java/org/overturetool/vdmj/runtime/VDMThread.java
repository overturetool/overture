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

package org.overturetool.vdmj.runtime;

import org.overturetool.vdmj.Settings;
import org.overturetool.vdmj.debug.DBGPReader;
import org.overturetool.vdmj.debug.DBGPReason;
import org.overturetool.vdmj.lex.Dialect;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.messages.RTLogger;
import org.overturetool.vdmj.values.CPUValue;
import org.overturetool.vdmj.values.ObjectValue;
import org.overturetool.vdmj.values.OperationValue;
import org.overturetool.vdmj.values.ValueList;

/**
 * A class representing a VDM thread.
 */

public class VDMThread extends Thread
{
	public final ObjectValue object;
	public final OperationValue operation;
	public final Context ctxt;
	public final String title;

	public VDMThread(LexLocation location, ObjectValue object, Context ctxt)
		throws ValueException
	{
		super("Object #" + object.objectReference);

		this.title =
			"Thread " + getId() +
			", self #" + object.objectReference +
			", class " + object.type.name.name;

		this.object = object;
		this.ctxt = new ObjectContext(location, title, ctxt.getGlobal(), object);
		this.operation = object.getThreadOperation(ctxt);

		VDMThreadSet.add(this);

		if (Settings.dialect == Dialect.VDM_RT)
		{
			CPUValue cpu = object.getCPU();
			cpu.addThread(this, object);

			RTLogger.log(
				"ThreadCreate -> id: " + getId() +
				" period: false objref: " + object.objectReference +
				" clnm: \"" + object.type + "\"" +
				" cpunm: " + cpu.cpuNumber +
				" time: " + VDMThreadSet.getWallTime());
		}
	}

	@Override
	public int hashCode()
	{
		return (int)getId();
	}

	@Override
	public void run()
	{
		if (Settings.dialect == Dialect.VDM_RT)
		{
			CPUValue cpu = object.getCPU();
			cpu.reschedule(RunState.RUNNABLE);
		}

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
			ctxt.setThreadState(null, operation.getCPU());
			operation.eval(new ValueList(), ctxt);
		}
		catch (StopException e)
		{
			// OK...
		}
		catch (ValueException e)
		{
			Interpreter.stop(e, e.ctxt);
		}
		catch (ContextException e)
		{
			Interpreter.stop(e, e.ctxt);
		}
		catch (Exception e)
		{
			Interpreter.stop(e, ctxt);
		}
		finally
		{
			VDMThreadSet.remove(this);
		}
	}

	private void runDBGP()
	{
		DBGPReader reader = null;

		try
		{
			reader = ctxt.threadState.dbgp.newThread();
			ctxt.setThreadState(reader, operation.getCPU());
			operation.eval(new ValueList(), ctxt);
			reader.complete(DBGPReason.OK, null);
		}
		catch (ContextException e)
		{
			reader.complete(DBGPReason.EXCEPTION, e);
		}
		catch (Exception e)
		{
			if (reader != null)
			{
				reader.complete(DBGPReason.EXCEPTION, null);
			}
		}
		finally
		{
			VDMThreadSet.remove(this);
		}
	}

	public synchronized void block()
	{
		ctxt.threadState.action = InterruptAction.SUSPENDED;
		interrupt();
	}

	public synchronized void unblock()
	{
		ctxt.threadState.action = InterruptAction.RUNNING;
		interrupt();
	}

	public synchronized void abort()
	{
		ctxt.threadState.action = InterruptAction.STOPPING;
		interrupt();
	}

	@Override
	public String toString()
	{
		return title + ", state " + ctxt.threadState.action;
	}
}
