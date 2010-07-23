/*******************************************************************************
 *
 *	Copyright (c) 2010 Fujitsu Services Ltd.
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

import java.util.Random;

import org.overturetool.vdmj.Settings;
import org.overturetool.vdmj.commands.DebuggerReader;
import org.overturetool.vdmj.debug.DBGPReader;
import org.overturetool.vdmj.debug.DBGPReason;
import org.overturetool.vdmj.lex.Dialect;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.runtime.ClassInterpreter;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.ContextException;
import org.overturetool.vdmj.runtime.ObjectContext;
import org.overturetool.vdmj.runtime.RootContext;
import org.overturetool.vdmj.runtime.ValueException;
import org.overturetool.vdmj.values.ObjectValue;
import org.overturetool.vdmj.values.OperationValue;
import org.overturetool.vdmj.values.TransactionValue;
import org.overturetool.vdmj.values.ValueList;

public class PeriodicThread extends SchedulablePoolThread
{
	private static final long serialVersionUID = 1L;
	private final OperationValue operation;
	private final long period;
	private final long jitter;
	private final long delay;
	private final long offset;
	private final long expected;

	private final boolean first;
	private final static Random PRNG = new Random();

	private boolean running = false;

	public PeriodicThread(
		ObjectValue self, OperationValue operation,
		long period, long jitter, long delay, long offset, long expected)
	{
		super(self.getCPU().resource, self, operation.getPriority(), true, expected);

		setName("PeriodicThread-" + getId());

		this.operation = operation;
		this.period = period;
		this.jitter = jitter;
		this.delay = delay;
		this.offset = offset;

		if (expected == 0)
		{
			this.first = true;
			this.expected = SystemClock.getWallTime();
		}
		else
		{
			this.first = false;
			this.expected = expected;
		}
	}
	
	@Override
	protected void body()
	{
		RootContext global = ClassInterpreter.getInstance().initialContext;
		LexLocation from = object.type.classdef.location;
		Context ctxt = new ObjectContext(from, "async", global, object);
		
		if(Settings.usingDBGP)
		{
			DBGPReader reader = ctxt.threadState.dbgp.newThread(object.getCPU());
			ctxt.setThreadState(reader,object.getCPU());
		}else
		{
			ctxt.setThreadState(null, object.getCPU());
		}

		if (first)
		{
			if (offset > 0 || jitter > 0)
			{
    			long noise = (jitter == 0) ? 0 :
    				Math.abs(PRNG.nextLong() % (jitter + 1));
    			waitUntil(offset + noise, ctxt, operation.name.location);
			}
		}
		else
		{
			waitUntil(expected, ctxt, operation.name.location);
		}

		new PeriodicThread(
			getObject(), operation, period, jitter, delay, 0,
			nextTime()).start();

		running = true;

		if (Settings.usingDBGP)
		{
			runDBGP(ctxt);
		}
		else
		{
			runCmd(ctxt);
		}

	}

	
	private void runDBGP(Context ctxt)
	{
		try
		{
    		try
    		{
        		operation.localEval(
        			operation.name.location, new ValueList(), ctxt, true);
        		
        		ctxt.threadState.dbgp.complete(DBGPReason.OK, null);
    		}
    		catch (ValueException e)
    		{
    			ctxt.threadState.dbgp.complete(DBGPReason.OK, new ContextException(e, operation.name.location));
    			throw new ContextException(e, operation.name.location);
    		}

			
		}
		catch (ContextException e)
		{
			suspendOthers();
			ResourceScheduler.setException(e);
			ctxt.threadState.dbgp.stopped(e.ctxt, e.location);
		}
		catch (Exception e)
		{
			ResourceScheduler.setException(e);
			BasicSchedulableThread.signalAll(Signal.SUSPEND);
		}
		finally
		{
			TransactionValue.commitAll();
		}
	}
	
	private void runCmd(Context ctxt)
	{
		try
		{
    		ctxt.setThreadState(null, object.getCPU());

    		operation.localEval(
    			operation.name.location, new ValueList(), ctxt, true);
		}
		catch (ValueException e)
		{
			suspendOthers();
			ResourceScheduler.setException(e);
			DebuggerReader.stopped(e.ctxt, operation.name.location);
		}
		catch (ContextException e)
		{
			suspendOthers();
			ResourceScheduler.setException(e);
			DebuggerReader.stopped(e.ctxt, operation.name.location);
		}
		catch (Exception e)
		{
			ResourceScheduler.setException(e);
			BasicSchedulableThread.signalAll(Signal.SUSPEND);
		}
		finally
		{
			TransactionValue.commitAll();
		}
	}

	private long nextTime()
	{
		// "expected" was last run time, the next is one "period" away, but this
		// is influenced by jitter as long as it's at least "delay" since
		// "expected".

		long noise = (jitter == 0) ? 0 : PRNG.nextLong() % (jitter + 1);
		long next = SystemClock.getWallTime() + period + noise;

		if (delay > 0 && next - expected < delay)	// Too close?
		{
			next = expected + delay;
		}

		return next;
	}

	private void waitUntil(long until, Context ctxt, LexLocation location)
	{
		long time = SystemClock.getWallTime();

		if (Settings.dialect == Dialect.VDM_RT)
		{
    		if (until > time)
    		{
    			duration(until - time, ctxt, location);
    		}
		}
		else
		{
    		while (until > time)
    		{
    			reschedule(ctxt, location);
    			time = SystemClock.getWallTime();
    		}
		}
	}

	public static void reset()
	{
		PRNG.setSeed(123);
	}

	@Override
	public boolean isActive()
	{
		// The initial timestep does not count as a deadlock wait

		return (running && state == RunState.TIMESTEP) ||
				state == RunState.WAITING;
	}
}
