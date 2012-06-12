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

package org.overture.interpreter.scheduler;

import java.util.Random;

import org.overture.ast.lex.Dialect;
import org.overture.config.Settings;
import org.overture.interpreter.debug.DBGPReader;
import org.overture.interpreter.runtime.ClassInterpreter;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.ContextException;
import org.overture.interpreter.runtime.ObjectContext;
import org.overture.interpreter.runtime.ValueException;
import org.overture.interpreter.values.ObjectValue;
import org.overture.interpreter.values.OperationValue;


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
	public void start()
	{
		super.start();

		// Here we put the thread into ALARM state (rather than RUNNABLE) and
		// set the time at which we want to be runnable to the expected start,
		// which may have an offset.

		if (Settings.dialect == Dialect.VDM_RT)
		{
    		long wakeUpTime = expected;

    		if (first)
    		{
    			if (offset > 0 || jitter > 0)
    			{
        			long noise = (jitter == 0) ? 0 :
        				Math.abs(PRNG.nextLong() % (jitter + 1));

        			wakeUpTime = offset + noise;
    			}
    		}

    		alarming(wakeUpTime);
		}
	}

	@Override
	protected void body()
	{
		RootContext global = ClassInterpreter.getInstance().initialContext;
		LexLocation from = object.type.classdef.location;
		Context ctxt = new ObjectContext(from, "async", global, object);

		if (Settings.dialect == Dialect.VDM_PP)
		{
			// VDM++ does not use the ALARM wakeup method of  VDM-RT, so
			// we make a busy wait until the time is expected.

			waitUntil(expected, ctxt, operation.name.location);
		}

		if (Settings.usingDBGP)
		{
			DBGPReader reader = ctxt.threadState.dbgp.newThread(object.getCPU());
			ctxt.setThreadState(reader,object.getCPU());
		}
		else
		{
			ctxt.setThreadState(null, object.getCPU());
		}

		new PeriodicThread(
			getObject(), operation, period, jitter, delay, 0,
			nextTime()).start();

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
    			int overlaps = object.incPeriodicCount();

    			if (Properties.rt_max_periodic_overlaps > 0 &&
    				overlaps >= Properties.rt_max_periodic_overlaps)
    			{
    				throw new ContextException(68, "Periodic threads overlapping", operation.name.location, ctxt);
    			}

        		operation.localEval(
        			operation.name.location, new ValueList(), ctxt, true);

        		ctxt.threadState.dbgp.complete(DBGPReason.OK, null);
        		object.decPeriodicCount();
    		}
    		catch (ValueException e)
    		{
    			ctxt.threadState.dbgp.complete(DBGPReason.OK, new ContextException(e, operation.name.location));
    			throw new ContextException(e, operation.name.location);
    		}
		}
		catch (ContextException e)
		{
			ResourceScheduler.setException(e);
			//suspendOthers();
			setExceptionOthers();
			ctxt.threadState.dbgp.stopped(e.ctxt, e.location);
		}
		catch (Exception e)
		{
			ResourceScheduler.setException(e);
			ctxt.threadState.dbgp.setErrorState();
			BasicSchedulableThread.signalAll(Signal.ERROR);
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
			int overlaps = object.incPeriodicCount();

			if (Properties.rt_max_periodic_overlaps > 0 &&
				overlaps >= Properties.rt_max_periodic_overlaps)
			{
				throw new ContextException(68, "Periodic threads overlapping", operation.name.location, ctxt);
			}

    		ctxt.setThreadState(null, object.getCPU());

    		operation.localEval(
    			operation.name.location, new ValueList(), ctxt, true);

    		object.decPeriodicCount();
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

	private void waitUntil(long until, Context ctxt, LexLocation location)
	{
		long time = SystemClock.getWallTime();

		while (until > time)
		{
			reschedule(ctxt, location);
			time = SystemClock.getWallTime();
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

	public static void reset()
	{
		PRNG.setSeed(123);
	}

	@Override
	public boolean isActive()
	{
		// The initial ALARM wait does not count as a deadlock wait

		return state == RunState.TIMESTEP || state == RunState.WAITING;
	}
}
