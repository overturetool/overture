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

package org.overture.interpreter.scheduler;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.overture.interpreter.values.TransactionValue;
import org.overture.parser.config.Properties;

public class FCFSPolicy extends SchedulingPolicy
{
	private static final long serialVersionUID = 1L;
	protected final List<ISchedulableThread> threads;
	protected ISchedulableThread bestThread = null;
	protected Random PRNG = null;

	private ISchedulableThread durationThread = null;

	public FCFSPolicy()
	{
		threads = new LinkedList<>();
		PRNG = new Random(); // NB deliberately non-deterministic!
	}

	@Override
	public void reset()
	{
		synchronized (threads)
		{
			threads.clear();
		}

		bestThread = null;
		durationThread = null;
	}

	@Override
	public void register(ISchedulableThread thread, long priority)
	{
		synchronized (threads)
		{
			// The last thread is the one currently running, so insert ahead of this
			// one so that the new thread is scheduled before the current one is
			// next scheduled.
			int count = threads.size();

			if (count == 0)
			{
				threads.add(thread);
			} else
			{
				threads.add(count - 1, thread);
			}
		}
	}

	@Override
	public void unregister(ISchedulableThread thread)
	{
		synchronized (threads)
		{
			threads.remove(thread);

			if (durationThread == thread)
			{
				durationThread = null;
			}
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
			for (ISchedulableThread th : threads)
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
		synchronized (threads) // As it was set under threads
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
		} else
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
	public long timeToNextAlarm()
	{
		long minTime = Long.MAX_VALUE;
		long now = SystemClock.getWallTime();

		synchronized (threads)
		{
			for (ISchedulableThread th : threads)
			{
				switch (th.getRunState())
				{
					case ALARM:
						long delay = th.getAlarmWakeTime() - now;

						if (delay < 0)
						{
							delay = 0; // Time has past
						}

						if (delay < minTime)
						{
							minTime = delay;
						}
						break;

					default:
						break;
				}
			}
		}

		return minTime;
	}

	@Override
	public void advance()
	{
		synchronized (threads)
		{
			for (ISchedulableThread th : threads)
			{
				switch (th.getRunState())
				{
					case TIMESTEP:
						durationThread = th;
						th.setState(RunState.RUNNABLE);

						if (Properties.rt_duration_transactions
								&& th.getDurationEnd() == SystemClock.getWallTime())
						{
							TransactionValue.commitOne(th.getId());
						}
						break;

					case ALARM:
						if (th.getAlarmWakeTime() <= SystemClock.getWallTime())
						{
							th.clearAlarm(); // Time to wake up!
							th.setState(RunState.RUNNABLE);
						}
						break;

					default:
						break;
				}
			}
		}
	}

	@Override
	public boolean hasActive()
	{
		synchronized (threads)
		{
			for (ISchedulableThread th : threads)
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

		for (ISchedulableThread th : threads)
		{
			sb.append(sep);
			sb.append(th);
			sep = "\n";
		}

		return sb.toString();
	}
}
