/*******************************************************************************
 *
 *	Overture.
 *
 *	Author: Kenneth Lausdahl
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

import java.io.Serializable;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.lex.Dialect;
import org.overture.ast.messages.InternalException;
import org.overture.config.Settings;
import org.overture.interpreter.messages.rtlog.RTExtendedTextMessage;
import org.overture.interpreter.messages.rtlog.RTLogger;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.values.ObjectValue;
import org.overture.parser.config.Properties;

public abstract class SchedulablePoolThread implements Serializable, Runnable,
		ISchedulableThread
{
	/**
	 * VdmThreadPoolExecutor used to set the Thread instance which will run a SchedulablePoolThread just before
	 * execution. It also reports a reject error if the pool no longer can expand to handle the requested number of
	 * threads
	 * 
	 * @author kela
	 */
	public static class VdmThreadPoolExecutor extends ThreadPoolExecutor
	{
		public VdmThreadPoolExecutor(int corePoolSize, int maximumPoolSize,
				long keepAliveTime, TimeUnit unit,
				BlockingQueue<Runnable> workQueue)
		{
			super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, new VdmjRejectedExecutionHandler());
		}

		@Override
		protected void beforeExecute(Thread t, Runnable r)
		{
			super.beforeExecute(t, r);
			if (r instanceof SchedulablePoolThread)
			{
				SchedulablePoolThread spt = (SchedulablePoolThread) r;
				spt.setThread(t);
				t.setName(spt.getName());
			}

		}

		@Override
		protected void afterExecute(Runnable r, Throwable t)
		{
			if (r instanceof SchedulablePoolThread)
			{
				SchedulablePoolThread spt = (SchedulablePoolThread) r;
				spt.setThread(null);
			}
		}

		/**
		 * Prints an error message if a execution is rejected
		 * 
		 * @author kela
		 */
		private static class VdmjRejectedExecutionHandler implements
				RejectedExecutionHandler
		{
			public void rejectedExecution(Runnable r,
					ThreadPoolExecutor executor)
			{
				System.err.println("Thread pool rejected thread: "
						+ ((ISchedulableThread) r).getName() + " pool size "
						+ executor.getActiveCount());
				throw new RejectedExecutionException();
			}

		}
	}

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
	private long alarmWakeTime;
	private long swapInBy;
	private boolean inOuterTimeStep;
	private Thread executingThread;
	private char name[];
	private long tid = 0;
	protected boolean stopCalled;

	/**
	 * Thread pool used by SchedulablePoolThread. It is a none blocking queue with an upper limit set to
	 * Integer.MAX_VALUE allowing it to freely expand. The thread pool will most likely make the Java VM throw
	 * OutOfMemoryError before Integer.MAX_VALUE is reached do the the native thread creation requiring 2 MB for each
	 * thread. <a href="http://java.sun.com/j2se/1.5.0/docs/api/java/util/concurrent/ThreadPoolExecutor.html">link</a>
	 */
	public final static VdmThreadPoolExecutor pool = new VdmThreadPoolExecutor(200, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<>());// LinkedBlockingQueue

	public SchedulablePoolThread(Resource resource, ObjectValue object,
			long priority, boolean periodic, long swapInBy)
	{
		this.tid = BasicSchedulableThread.nextThreadID();
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
		alarmWakeTime = Long.MAX_VALUE;
		inOuterTimeStep = false;
		stopCalled = false;

		resource.register(this, priority);

		BasicSchedulableThread.add(this);

	}

	private void setThread(Thread t)
	{
		executingThread = t;
	}

	public Thread getThread()
	{
		return this.executingThread;
	}

	/*
	 * (non-Javadoc)
	 * @see org.overture.vdmj.scheduler.ISchedulableThread#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object other)
	{
		if (other instanceof ISchedulableThread)
		{
			ISchedulableThread to = (ISchedulableThread) other;
			return getId() == to.getId();
		}

		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see org.overture.vdmj.scheduler.ISchedulableThread#hashCode()
	 */
	@Override
	public int hashCode()
	{
		return new Long(getId()).hashCode();
	}

	/*
	 * (non-Javadoc)
	 * @see org.overture.vdmj.scheduler.ISchedulableThread#toString()
	 */
	@Override
	public String toString()
	{
		return getName() + " (" + (stopCalled ? "STOPPING" : state) + ")";
	}

	/*
	 * (non-Javadoc)
	 * @see org.overture.vdmj.scheduler.ISchedulableThread#start()
	 */

	public synchronized void start()
	{
		pool.execute(this);

		while (state == RunState.CREATED)
		{
			sleep(null, null);
		}

		// Log the creation here so that it is deterministic...

		if (resource instanceof CPUResource)
		{
			CPUResource cpu = (CPUResource) resource;
			cpu.createThread(this);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.overture.vdmj.scheduler.ISchedulableThread#run()
	 */

	public void run()
	{
		try
		{
			reschedule(null, null);
			body();
		} finally
		{
			setState(RunState.COMPLETE);
			resource.unregister(this);
			BasicSchedulableThread.remove(this);
			getThread().setName("pool-" + getThread().getId() + "-thread-");
		}
	}

	abstract protected void body();

	/*
	 * (non-Javadoc)
	 * @see org.overture.vdmj.scheduler.ISchedulableThread#step(org.overture.vdmj.runtime.Context,
	 * org.overture.vdmj.lex.ILexLocation)
	 */
	public void step(Context ctxt, ILexLocation location)
	{
		if (Settings.dialect == Dialect.VDM_RT)
		{
			if (!virtual)
			{
				duration(getObject().getCPU().getDuration(Properties.rt_cycle_default), ctxt, location);
			}
		} else
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

	/*
	 * (non-Javadoc)
	 * @see org.overture.vdmj.scheduler.ISchedulableThread#getRunState()
	 */
	public synchronized RunState getRunState()
	{
		return state;
	}

	/*
	 * (non-Javadoc)
	 * @see org.overture.vdmj.scheduler.ISchedulableThread#setState(org.overture.vdmj.scheduler.RunState)
	 */
	public synchronized void setState(RunState newstate)
	{
		state = newstate;
		notifyAll();
	}

	public synchronized void reschedule(Context ctxt, ILexLocation location)
	{
		// Yield control but remain runnable - called by thread
		waitUntilState(RunState.RUNNABLE, RunState.RUNNING, ctxt, location);
	}

	/*
	 * (non-Javadoc)
	 * @see org.overture.vdmj.scheduler.ISchedulableThread#waiting(org.overture.vdmj.runtime.Context,
	 * org.overture.vdmj.lex.ILexLocation)
	 */
	public synchronized void waiting(Context ctxt, ILexLocation location)
	{
		// Enter a waiting state - called by thread
		waitUntilState(RunState.WAITING, RunState.RUNNING, ctxt, location);
	}

	/*
	 * (non-Javadoc)
	 * @see org.overture.vdmj.scheduler.ISchedulableThread#locking(org.overture.vdmj.runtime.Context,
	 * org.overture.vdmj.lex.ILexLocation)
	 */
	public synchronized void locking(Context ctxt, ILexLocation location)
	{
		// Enter a locking state - called by thread
		waitUntilState(RunState.LOCKING, RunState.RUNNING, ctxt, location);
	}

	/*
	 * (non-Javadoc)
	 * @see org.overture.vdmj.scheduler.ISchedulableThread#alarming(long, Context, ILexLocation)
	 */
	public synchronized void alarming(long expected)
	{
		// Enter an alarming state - called by another thread, does not block.
		alarmWakeTime = expected;
		setState(RunState.ALARM);
	}

	/*
	 * (non-Javadoc)
	 * @see org.overture.vdmj.scheduler.ISchedulableThread#runslice(long)
	 */
	public synchronized void runslice(long slice)
	{
		// Run one time slice - called by Scheduler
		timeslice = slice;
		waitWhileState(RunState.RUNNING, RunState.RUNNING, null, null);
	}

	/*
	 * (non-Javadoc)
	 * @see org.overture.vdmj.scheduler.ISchedulableThread#duration(long, org.overture.vdmj.runtime.Context,
	 * org.overture.vdmj.lex.ILexLocation)
	 */
	public synchronized void duration(long pause, Context ctxt,
			ILexLocation location)
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
					RTLogger.log(new RTExtendedTextMessage(String.format("-- %s Waiting to move time by %d", this, timestep)));
				}

				waitUntilState(RunState.TIMESTEP, RunState.RUNNING, ctxt, location);
				setTimestep(durationEnd - SystemClock.getWallTime());
			} while (getTimestep() > 0);

			setTimestep(Long.MAX_VALUE); // Finished
		}
	}

	private synchronized void waitWhileState(RunState newstate,
			RunState whilestate, Context ctxt, ILexLocation location)
	{
		setState(newstate);

		while (state == whilestate)
		{
			sleep(ctxt, location);
		}
	}

	private synchronized void waitUntilState(RunState newstate, RunState until,
			Context ctxt, ILexLocation location)
	{
		setState(newstate);

		while (state != until)
		{
			sleep(ctxt, location);
		}
	}

	private synchronized void sleep(Context ctxt, ILexLocation location)
	{
		while (true)
		{
			try
			{
				wait();

				if (stopCalled && state == RunState.RUNNING)
				{
					// stopThread made us RUNNABLE, now we're running, so die
					throw new ThreadDeath();
				}

				return;
			} catch (InterruptedException e)
			{
				if (signal != null)
				{
					handleSignal(signal, ctxt, location);
				}
			}
		}
	}

	protected void handleSignal(Signal sig, Context ctxt, ILexLocation location)
	{
		BasicSchedulableThread.handleSignal(sig, ctxt, location);
	}

	/*
	 * (non-Javadoc)
	 * @see org.overture.vdmj.scheduler.ISchedulableThread#suspendOthers()
	 */
	public void suspendOthers()
	{
		BasicSchedulableThread.suspendOthers(this);
	}

	public void setExceptionOthers()
	{
		BasicSchedulableThread.setExceptionOthers(this);
	}

	public static void signalAll(Signal sig)
	{
		BasicSchedulableThread.signalAll(sig);
	}

	public synchronized boolean stopThread()
	{
		if (!stopCalled)
		{
			stopCalled = true;
			timestep = Long.MAX_VALUE; // Don't take part in time step

			if (Thread.currentThread() != this.getThread())
			{
				setState(RunState.RUNNABLE); // So that thread is rescheduled
			}

			return true;
		} else
		{
			return false;
		}
	}

	public synchronized void setSignal(Signal sig)
	{
		signal = sig;
		interrupt();
	}

	private void interrupt()
	{
		if (getThread() != null)
		{
			getThread().interrupt();
		} else
		{
			Thread.currentThread().interrupt();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.overture.vdmj.scheduler.ISchedulableThread#getObject()
	 */
	public ObjectValue getObject()
	{
		return object;
	}

	/*
	 * (non-Javadoc)
	 * @see org.overture.vdmj.scheduler.ISchedulableThread#setSwapInBy(long)
	 */
	public synchronized void setSwapInBy(long swapInBy)
	{
		this.swapInBy = swapInBy;
	}

	/*
	 * (non-Javadoc)
	 * @see org.overture.vdmj.scheduler.ISchedulableThread#getSwapInBy()
	 */
	public synchronized long getSwapInBy()
	{
		return swapInBy;
	}

	/*
	 * (non-Javadoc)
	 * @see org.overture.vdmj.scheduler.ISchedulableThread#isPeriodic()
	 */
	public boolean isPeriodic()
	{
		return periodic;
	}

	/*
	 * (non-Javadoc)
	 * @see org.overture.vdmj.scheduler.ISchedulableThread#isActive()
	 */
	public boolean isActive()
	{
		return state == RunState.TIMESTEP || state == RunState.WAITING;
	}

	/*
	 * (non-Javadoc)
	 * @see org.overture.vdmj.scheduler.ISchedulableThread#isVirtual()
	 */
	public boolean isVirtual()
	{
		return virtual;
	}

	/*
	 * (non-Javadoc)
	 * @see org.overture.vdmj.scheduler.ISchedulableThread#setTimestep(long)
	 */
	public synchronized void setTimestep(long step)
	{
		timestep = step;
	}

	/*
	 * (non-Javadoc)
	 * @see org.overture.vdmj.scheduler.ISchedulableThread#getTimestep()
	 */
	public synchronized long getTimestep()
	{
		return timestep;
	}

	/*
	 * (non-Javadoc)
	 * @see org.overture.vdmj.scheduler.ISchedulableThread#getDurationEnd()
	 */
	public synchronized long getDurationEnd()
	{
		return durationEnd;
	}

	/*
	 * (non-Javadoc)
	 * @see org.overture.vdmj.scheduler.ISchedulableThread#getAlarmWakeTime()
	 */
	public synchronized long getAlarmWakeTime()
	{
		return alarmWakeTime;
	}

	/*
	 * (non-Javadoc)
	 * @see org.overture.vdmj.scheduler.ISchedulableThread#clearAlarm()
	 */
	public void clearAlarm()
	{
		alarmWakeTime = Long.MAX_VALUE;
	}

	/*
	 * (non-Javadoc)
	 * @see org.overture.vdmj.scheduler.ISchedulableThread#inOuterTimestep(boolean)
	 */
	public synchronized void inOuterTimestep(boolean b)
	{
		inOuterTimeStep = b;
	}

	/*
	 * (non-Javadoc)
	 * @see org.overture.vdmj.scheduler.ISchedulableThread#inOuterTimestep()
	 */
	public synchronized boolean inOuterTimestep()
	{
		return inOuterTimeStep;
	}

	/*
	 * (non-Javadoc)
	 * @see org.overture.vdmj.scheduler.ISchedulableThread#getCPUResource()
	 */
	public CPUResource getCPUResource()
	{
		if (resource instanceof CPUResource)
		{
			return (CPUResource) resource;
		} else
		{
			throw new InternalException(66, "Thread is not running on a CPU");
		}
	}

	public long getId()
	{
		return tid;
	}

	public final String getName()
	{
		return String.valueOf(name);
	}

	public final void setName(String name)
	{
		this.name = name.toCharArray();
	}

	public boolean isAlive()
	{
		if (getThread() != null)
		{
			return getThread().isAlive();
		}
		return false;
	}

}
