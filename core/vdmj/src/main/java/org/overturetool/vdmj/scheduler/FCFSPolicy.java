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

package org.overturetool.vdmj.scheduler;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.overturetool.vdmj.config.Properties;
import org.overturetool.vdmj.values.TransactionValue;

public class FCFSPolicy extends SchedulingPolicy
{
    private static final long serialVersionUID = 1L;
	protected final List<ISchedulableThread> threads;
	protected ISchedulableThread bestThread = null;
	protected Random PRNG = null;

	private ISchedulableThread durationThread = null;

	public FCFSPolicy()
	{
		threads = new LinkedList<ISchedulableThread>();
		PRNG = new Random();	// NB deliberately non-deterministic!
	}

	@Override
	public void reset()
	{
		threads.clear();
		bestThread = null;
	}

	@Override
	public void register(ISchedulableThread thread, long priority)
	{
		synchronized (threads)
		{
			threads.add(thread);
		}
	}

	@Override
	public void unregister(ISchedulableThread thread)
	{
		synchronized (threads)
		{
			threads.remove(thread);
		}
	}

	@Override
	public boolean reschedule()
	{
		bestThread = null;

		if (durationThread != null)
		{
			bestThread = durationThread;
			durationThread = null;
			return true;
		}

		synchronized (threads)
		{
    		for (ISchedulableThread th: threads)
    		{
    			switch (th.getRunState())
    			{
    				case RUNNABLE:
        				bestThread = th;
        				threads.remove(th);
        				threads.add(th);
        				return true;

    				case TIMESTEP:
    				case WAITING:
    				case LOCKING:
    				default:
    					break;
    			}
    		}
		}

		return false;
	}

	@Override
	public ISchedulableThread getThread()
	{
		synchronized (threads)		// As it was set under threads
		{
			return bestThread;
		}
	}

	@Override
	public long getTimeslice()
	{
		long slice = 0;

		if (bestThread.isVirtual())
		{
			slice = Properties.scheduler_virtual_timeslice;
		}
		else
		{
			slice = Properties.scheduler_fcfs_timeslice;
		}

		if (Properties.scheduler_jitter > 0)
		{
			// Plus or minus jitter ticks...
			slice += PRNG.nextLong() % (Properties.scheduler_jitter + 1);
		}

		return slice;
	}

	@Override
	public void advance()
	{
		synchronized (threads)
		{
    		for (ISchedulableThread th: threads)
    		{
    			if (th.getRunState() == RunState.TIMESTEP)
    			{
    				durationThread = th;
    				th.setState(RunState.RUNNABLE);

    				if (Properties.rt_duration_transactions &&
    					th.getDurationEnd() == SystemClock.getWallTime())
    				{
    					TransactionValue.commitOne(th.getId());
    				}
    			}
    		}
		}
	}

	@Override
	public boolean hasActive()
	{
		synchronized (threads)
		{
    		for (ISchedulableThread th: threads)
    		{
    			if (th.isActive())
    			{
    				return true;
    			}
    		}
		}

		return false;
	}

	@Override
	public boolean hasPriorities()
	{
		return false;
	}

	@Override
	public String getStatus()
	{
		StringBuilder sb = new StringBuilder();
		String sep = "";

		for (ISchedulableThread th: threads)
		{
			sb.append(sep);
			sb.append(th);
			sep = "\n";
		}

		return sb.toString();
	}
}
