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

import java.util.Random;

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
	public final long offset;
	public final long period;
	public final long jitter;
	public final long delay;
	public final long expected;
	public final boolean first;

	private static boolean stopping = false;
	private static Random PRNG = new Random();

	public static synchronized void periodicStop()
	{
		stopping = true;
	}

	public static synchronized boolean stopping()
	{
		return stopping;
	}

	public static void reset()
	{
		stopping = false;
	}

	public AsyncThread(MessageRequest request)
	{
		this(request.target, request.operation, request.args, 0, 0, 0, 0, 0);
		this.request = request;
	}

	public AsyncThread(
		ObjectValue self, OperationValue operation, ValueList args,
		long period, long jitter, long delay, long offset, long expected)
	{
		setName("Async Thread " + getId());

		this.self = self;
		this.operation = operation;
		this.args = args;
		this.cpu = self.getCPU();
		this.period = period;
		this.jitter = jitter;
		this.delay = delay;
		this.offset = offset;
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

	private long nextTime()
	{
		// "expected" was last run time, the next is one "period" away, but this
		// is influenced by jitter as long as it's at least "delay" since
		// "expected".

		long noise = (jitter == 0) ? 0 : PRNG.nextLong() % (jitter + 1);
		long next = expected + period + noise;

		if (delay > 0 && next - expected < delay)	// Too close?
		{
			next = expected + delay;
		}

		return next;
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
    		boolean logreq = false;

    		if (period > 0 && !stopping)	// period = 0 is a one shot thread
    		{
    			if (first)
    			{
        			long noise = (jitter == 0) ? 0 :
        				Math.abs(new Random().nextLong() % (jitter + 1));

        			cpu.duration(offset + noise);
    			}
    			else
    			{
    				cpu.waitUntil(expected);
    			}

    			logreq = true;

    			new AsyncThread(
    				self, operation, new ValueList(), period, jitter, delay, offset,
    				nextTime()).start();
    		}

    		try
    		{
        		RootContext global = ClassInterpreter.getInstance().initialContext;
        		Context ctxt = new ObjectContext(operation.name.location, "async", global, self);
    			reader = ctxt.threadState.dbgp.newThread();
    			ctxt.setThreadState(reader, cpu);

        		Value rv = operation.localEval(args, ctxt, logreq);
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
			if (reader != null)
			{
				reader.complete(DBGPReason.OK, null);
			}
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
    		boolean logreq = false;

    		if (period > 0 && !stopping)	// period = 0 is a one shot thread
    		{
    			if (first)
    			{
    				if (offset > 0 || jitter > 0)
   					{
    					long noise = (jitter == 0) ? 0 :
    						Math.abs(new Random().nextLong() % (jitter + 1));

    					cpu.duration(offset + noise);
   					}
    			}
    			else
    			{
    				cpu.waitUntil(expected);
    			}

    			logreq = true;

    			new AsyncThread(
    				self, operation, new ValueList(), period, jitter, delay, offset,
    				nextTime()).start();
    		}

    		try
    		{
        		RootContext global = ClassInterpreter.getInstance().initialContext;
        		Context ctxt = new ObjectContext(operation.name.location, "async", global, self);
        		ctxt.setThreadState(null, cpu);

        		Value rv = operation.localEval(args, ctxt, logreq);
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
			Interpreter.stop(e.location, e, e.ctxt);
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
