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

import java.io.Serializable;

import org.overturetool.vdmj.Settings;
import org.overturetool.vdmj.commands.DebuggerReader;
import org.overturetool.vdmj.config.Properties;
import org.overturetool.vdmj.lex.Dialect;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.messages.InternalException;
import org.overturetool.vdmj.messages.RTLogger;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.values.ObjectValue;

public abstract class SchedulableThread extends Thread implements Serializable,Runnable, ISchedulableThread
{
    private static final long serialVersionUID = 1L;

	protected final Resource resource;
	protected final ObjectValue object;
	private final boolean periodic;
	private final boolean virtual;

	protected RunState state;
	private Signal signal;
	private long timeslice;
	private long steps;
	private long timestep;
	private long durationEnd;
	private long swapInBy;
	private boolean inOuterTimeStep;
	private long tid = 0;
	public SchedulableThread(
		Resource resource, ObjectValue object, long priority,
		boolean periodic, long swapInBy)
	{
		this.tid =BasicSchedulableThread.nextThreadID();
		this.resource = resource;
		this.object = object;
		this.periodic = periodic;
		this.virtual = resource.isVirtual();
		this.setSwapInBy(swapInBy);

		state = RunState.CREATED;
		signal = null;
		timeslice = 0;
		steps = 0;
		timestep = Long.MAX_VALUE;
		durationEnd = 0;
		inOuterTimeStep = false;

		resource.register(this, priority);

		BasicSchedulableThread.add(this);
	}

	/* (non-Javadoc)
	 * @see org.overturetool.vdmj.scheduler.ISchedulableThread#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object other)
	{
		if (other instanceof ISchedulableThread)
		{
			ISchedulableThread to = (ISchedulableThread)other;
			return getId() == to.getId();
		}

		return false;
	}

	/* (non-Javadoc)
	 * @see org.overturetool.vdmj.scheduler.ISchedulableThread#hashCode()
	 */
	@Override
	public int hashCode()
	{
		return Long.valueOf(getId()).hashCode();
	}

	/* (non-Javadoc)
	 * @see org.overturetool.vdmj.scheduler.ISchedulableThread#toString()
	 */
	@Override
	public String toString()
	{
		return getName() + " (" + state + ")";
	}

    /* (non-Javadoc)
	 * @see org.overturetool.vdmj.scheduler.ISchedulableThread#start()
	 */
    @Override
	public synchronized void start()
	{
		super.start();

		while (state == RunState.CREATED)
		{
			sleep(null, null);
		}

		// Log the creation here so that it is deterministic...

		if (resource instanceof CPUResource)
		{
			CPUResource cpu = (CPUResource)resource;
			cpu.createThread(this);
		}
	}

	/* (non-Javadoc)
	 * @see org.overturetool.vdmj.scheduler.ISchedulableThread#run()
	 */
	@Override
	public void run()
	{
		reschedule(null, null);
		body();
		setState(RunState.COMPLETE);
		resource.unregister(this);

		BasicSchedulableThread.remove(this);
	}

	abstract protected void body();

	/* (non-Javadoc)
	 * @see org.overturetool.vdmj.scheduler.ISchedulableThread#step(org.overturetool.vdmj.runtime.Context, org.overturetool.vdmj.lex.LexLocation)
	 */
	public void step(Context ctxt, LexLocation location)
	{
		if (Settings.dialect == Dialect.VDM_RT)
		{
			if (!virtual)
			{
				duration(Properties.rt_duration_default, ctxt, location);
			}
		}
		else
		{
			SystemClock.advance(Properties.rt_duration_default);
		}

		// Note that we don't reschedule if we are in an outer cycles/duration

		if (++steps >= timeslice && !inOuterTimeStep)
		{
			reschedule(ctxt, location);
			steps = 0;
		}
	}

	/* (non-Javadoc)
	 * @see org.overturetool.vdmj.scheduler.ISchedulableThread#getRunState()
	 */
	public synchronized RunState getRunState()
	{
		return state;
	}

	/* (non-Javadoc)
	 * @see org.overturetool.vdmj.scheduler.ISchedulableThread#setState(org.overturetool.vdmj.scheduler.RunState)
	 */
	public synchronized void setState(RunState newstate)
	{
		state = newstate;
		notifyAll();
	}

	protected synchronized void reschedule(Context ctxt, LexLocation location)
	{
		// Yield control but remain runnable - called by thread
		waitUntilState(RunState.RUNNABLE, RunState.RUNNING, ctxt, location);
	}

	/* (non-Javadoc)
	 * @see org.overturetool.vdmj.scheduler.ISchedulableThread#waiting(org.overturetool.vdmj.runtime.Context, org.overturetool.vdmj.lex.LexLocation)
	 */
	public synchronized void waiting(Context ctxt, LexLocation location)
	{
		// Enter a waiting state - called by thread
		waitUntilState(RunState.WAITING, RunState.RUNNING, ctxt, location);
	}

	/* (non-Javadoc)
	 * @see org.overturetool.vdmj.scheduler.ISchedulableThread#locking(org.overturetool.vdmj.runtime.Context, org.overturetool.vdmj.lex.LexLocation)
	 */
	public synchronized void locking(Context ctxt, LexLocation location)
	{
		// Enter a locking state - called by thread
		waitUntilState(RunState.LOCKING, RunState.RUNNING, ctxt, location);
	}

	/* (non-Javadoc)
	 * @see org.overturetool.vdmj.scheduler.ISchedulableThread#runslice(long)
	 */
	public synchronized void runslice(long slice)
	{
		// Run one time slice - called by Scheduler
		timeslice = slice;
		waitWhileState(RunState.RUNNING, RunState.RUNNING, null, null);
	}

	/* (non-Javadoc)
	 * @see org.overturetool.vdmj.scheduler.ISchedulableThread#duration(long, org.overturetool.vdmj.runtime.Context, org.overturetool.vdmj.lex.LexLocation)
	 */
	public synchronized void duration(
		long pause, Context ctxt, LexLocation location)
	{
		// Wait until pause has passed - called by thread

		if (!inOuterTimeStep)
		{
    		setTimestep(pause);
    		durationEnd = SystemClock.getWallTime() + pause;

    		do
    		{
        		if (Properties.diags_timestep)
        		{
        			RTLogger.log(String.format("-- %s Waiting to move time by %d",
        				this, timestep));
        		}

        		waitUntilState(RunState.TIMESTEP, RunState.RUNNING, ctxt, location);
    			setTimestep(durationEnd - SystemClock.getWallTime());
    		}
    		while (getTimestep() > 0);

    		setTimestep(Long.MAX_VALUE);	// Finished
		}
	}

	private synchronized void waitWhileState(
		RunState newstate, RunState until, Context ctxt, LexLocation location)
	{
		setState(newstate);

		while (state == until)
		{
			sleep(ctxt, location);
		}
	}

	private synchronized void waitUntilState(
		RunState newstate, RunState until, Context ctxt, LexLocation location)
	{
		setState(newstate);

		while (state != until)
		{
			sleep(ctxt, location);
		}
	}

	private synchronized void sleep(Context ctxt, LexLocation location)
	{
		while (true)
		{
    		try
    		{
   				wait();
 				return;
    		}
    		catch (InterruptedException e)
    		{
    			handleSignal(signal, ctxt, location);
    		}
		}
	}

	protected void handleSignal(Signal sig, Context ctxt, LexLocation location)
	{
		switch (sig)
		{
			case TERMINATE:
				throw new ThreadDeath();

			case SUSPEND:
			case DEADLOCKED:
				if (ctxt != null)
				{
    				if (Settings.usingDBGP)
    				{
    					ctxt.threadState.dbgp.stopped(ctxt, location);
    				}
    				else
    				{
    					DebuggerReader.stopped(ctxt, location);
    				}

    				if (sig == Signal.DEADLOCKED)
    				{
    					throw new ThreadDeath();
    				}
				}
				break;
		}
	}

	/* (non-Javadoc)
	 * @see org.overturetool.vdmj.scheduler.ISchedulableThread#suspendOthers()
	 */
	public void suspendOthers()
	{
		BasicSchedulableThread.suspendOthers(this);
	}

	public static void signalAll(Signal sig)
	{
		BasicSchedulableThread.signalAll(sig);
	}

	public synchronized void setSignal(Signal sig)
	{
		signal = sig;
		interrupt();
	}

	/* (non-Javadoc)
	 * @see org.overturetool.vdmj.scheduler.ISchedulableThread#getObject()
	 */
	public ObjectValue getObject()
	{
		return object;
	}

	/* (non-Javadoc)
	 * @see org.overturetool.vdmj.scheduler.ISchedulableThread#setSwapInBy(long)
	 */
	public synchronized void setSwapInBy(long swapInBy)
	{
		this.swapInBy = swapInBy;
	}

	/* (non-Javadoc)
	 * @see org.overturetool.vdmj.scheduler.ISchedulableThread#getSwapInBy()
	 */
	public synchronized long getSwapInBy()
	{
		return swapInBy;
	}

	/* (non-Javadoc)
	 * @see org.overturetool.vdmj.scheduler.ISchedulableThread#isPeriodic()
	 */
	public boolean isPeriodic()
	{
		return periodic;
	}

	/* (non-Javadoc)
	 * @see org.overturetool.vdmj.scheduler.ISchedulableThread#isActive()
	 */
	public boolean isActive()
	{
		return state == RunState.TIMESTEP || state == RunState.WAITING;
	}

	/* (non-Javadoc)
	 * @see org.overturetool.vdmj.scheduler.ISchedulableThread#isVirtual()
	 */
	public boolean isVirtual()
	{
		return virtual;
	}

	/* (non-Javadoc)
	 * @see org.overturetool.vdmj.scheduler.ISchedulableThread#setTimestep(long)
	 */
	public synchronized void setTimestep(long step)
	{
		timestep = step;
	}

	/* (non-Javadoc)
	 * @see org.overturetool.vdmj.scheduler.ISchedulableThread#getTimestep()
	 */
	public synchronized long getTimestep()
	{
		return timestep;
	}

	/* (non-Javadoc)
	 * @see org.overturetool.vdmj.scheduler.ISchedulableThread#getDurationEnd()
	 */
	public synchronized long getDurationEnd()
	{
		return durationEnd;
	}

	/* (non-Javadoc)
	 * @see org.overturetool.vdmj.scheduler.ISchedulableThread#inOuterTimestep(boolean)
	 */
	public synchronized void inOuterTimestep(boolean b)
	{
		inOuterTimeStep = b;
	}

	/* (non-Javadoc)
	 * @see org.overturetool.vdmj.scheduler.ISchedulableThread#inOuterTimestep()
	 */
	public synchronized boolean inOuterTimestep()
	{
		return inOuterTimeStep;
	}

	/* (non-Javadoc)
	 * @see org.overturetool.vdmj.scheduler.ISchedulableThread#getCPUResource()
	 */
	public CPUResource getCPUResource()
	{
		if (resource instanceof CPUResource)
		{
			return (CPUResource)resource;
		}
		else
		{
			throw new InternalException(66, "Thread is not running on a CPU");
		}
	}
	
	public Thread getThread()
	{
		return this;
	}
	

	@Override
	public long getId()
	{
		return this.tid;
	}
}
