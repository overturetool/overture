/*******************************************************************************
 *
 *	Copyright (c) 2009 Fujitsu Services Ltd.
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
import org.overturetool.vdmj.values.CPUValue;
import org.overturetool.vdmj.values.ObjectValue;
import org.overturetool.vdmj.values.OperationValue;
import org.overturetool.vdmj.values.Value;
import org.overturetool.vdmj.values.ValueList;

public class AsyncThread extends Thread
{
	private MessageRequest request = null;

	public final ObjectValue self;
	public final OperationValue operation;
	public final ValueList args;
	public final CPUValue cpu;
	public final long period;
	public final long expected;
	public final boolean first;

	public AsyncThread(MessageRequest request)
	{
		this(request.target, request.operation, request.args, 0, 0);
		this.request = request;
	}

	public AsyncThread(
		ObjectValue self, OperationValue operation, ValueList args,
		long period, long expected)
	{
		setName("Async Thread " + getId());

		this.self = self;
		this.operation = operation;
		this.args = args;
		this.cpu = self.getCPU();
		this.period = period;
		this.request = new MessageRequest();

		if (period > 0 && expected == 0)
		{
			this.first = true;
			this.expected = SystemClock.getWallTime();
		}
		else
		{
			this.first = false;
			this.expected = expected;
		}

		cpu.addThread(this, self, operation, period > 0);
	}

	@Override
	public void run()
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

	private void runDBGP()
	{
		DBGPReader reader = null;

		try
		{
			cpu.startThread(expected);
    		MessageResponse response = null;

    		if (period > 0)
    		{
    			if (!first)
    			{
    				cpu.duration(period);
    			}

    			new AsyncThread(
    				self, operation, new ValueList(), period, expected + period).start();
    		}

    		try
    		{
        		RootContext global = ClassInterpreter.getInstance().initialContext;
        		Context ctxt = new ObjectContext(operation.name.location, "async", global, self);
    			reader = ctxt.threadState.dbgp.newThread();
    			ctxt.setThreadState(reader, cpu);

        		Value rv = operation.localEval(args, ctxt, false);
       			response = new MessageResponse(rv, request);
    		}
    		catch (ValueException e)
    		{
    			response = new MessageResponse(e, request);
    		}

    		if (request.replyTo != null)
    		{
    			request.bus.reply(response);
    		}

			reader.complete(DBGPReason.OK, null);
			cpu.removeThread();
		}
		catch (ContextException e)
		{
			reader.complete(DBGPReason.EXCEPTION, e);
		}
		catch (RTException e)
		{
			// Thread stopped
		}
		catch (Exception e)
		{
			if (reader != null)
			{
				reader.complete(DBGPReason.EXCEPTION, null);
			}
		}
	}

	private void runCmd()
	{
		try
		{
			cpu.startThread(expected);
    		MessageResponse response = null;

    		if (period > 0)
    		{
    			if (!first)
    			{
    				cpu.duration(period);
    			}

    			new AsyncThread(
    				self, operation, new ValueList(), period, expected + period).start();
    		}

    		try
    		{
        		RootContext global = ClassInterpreter.getInstance().initialContext;
        		Context ctxt = new ObjectContext(operation.name.location, "async", global, self);
        		ctxt.setThreadState(null, cpu);

        		Value rv = operation.localEval(args, ctxt, false);
       			response = new MessageResponse(rv, request);
    		}
    		catch (ValueException e)
    		{
    			response = new MessageResponse(e, request);
    		}

    		if (request.replyTo != null)
    		{
    			request.bus.reply(response);
    		}

    		cpu.removeThread();
		}
		catch (ContextException e)
		{
			Interpreter.stop(e, e.ctxt);
		}
		catch (RTException e)
		{
			// Thread stopped
		}
	}

	@Override
	public int hashCode()
	{
		return (int)getId();
	}
}
