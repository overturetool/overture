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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class FCFSPolicy extends SchedulingPolicy
{
	private static final long TIMESLICE = 100;
	private final List<Thread> threads;
	private final Map<Thread, RunState> state;

	private Thread bestThread = null;

	public FCFSPolicy()
	{
		this.threads = new LinkedList<Thread>();
		this.state = new HashMap<Thread, RunState>();
	}

	@Override
	public synchronized void addThread(Thread thread)
	{
		threads.add(thread);
		state.put(thread, RunState.CREATED);
	}

	@Override
	public synchronized void removeThread(Thread thread)
	{
		threads.remove(thread);
		state.remove(thread);

		if (bestThread == thread)
		{
			reschedule();
		}
	}

	@Override
	public synchronized Thread getThread()
	{
		return bestThread;
	}

	@Override
	public synchronized void setState(Thread thread, RunState newstate)
	{
		state.put(thread, newstate);
	}

	@Override
	public synchronized boolean reschedule()
	{
		Thread original = bestThread;
		bestThread = null;

		for (Thread th: threads)	// Queue order
		{
			if (state.get(th) == RunState.RUNNABLE)
			{
				bestThread = th;
				threads.remove(th);
				threads.add(th);	// Add at the end of the queue
				break;
			}
		}

		return (bestThread != original);
	}

	@Override
	public boolean canTimeStep()
	{
		for (Thread th: threads)
		{
			if (state.get(th) != RunState.TIMESTEP)
			{
				return false;
			}
		}

		return true;
	}

	@Override
	public long getTimeslice()
	{
		return TIMESLICE;
	}
}
