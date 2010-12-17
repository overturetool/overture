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

import org.overturetool.vdmj.messages.rtlog.RTDeployObjectMessage;
import org.overturetool.vdmj.messages.rtlog.RTLogger;
import org.overturetool.vdmj.messages.rtlog.RTThreadCreateMessage;
import org.overturetool.vdmj.messages.rtlog.RTThreadKillMessage;
import org.overturetool.vdmj.messages.rtlog.RTThreadSwapMessage;
import org.overturetool.vdmj.messages.rtlog.RTThreadSwapMessage.SwapType;
import org.overturetool.vdmj.scheduler.SystemClock.TimeUnit;
import org.overturetool.vdmj.values.ObjectValue;

public class CPUResource extends Resource
{
    private static final long serialVersionUID = 1L;
	private static int nextCPU = 0;
	public static CPUResource vCPU = null;

	private final int cpuNumber;
	private final double clock;

	private ISchedulableThread swappedIn = null;

	public CPUResource(SchedulingPolicy policy, double clock)
	{
		super(policy);
		this.cpuNumber = nextCPU++;
		this.clock = clock;

		swappedIn = null;

		if (cpuNumber == 0)
		{
			vCPU = this;
		}
	}

	public static void init()
	{
		nextCPU = 0;
		vCPU = null;
	}

	@Override
	public void reset()
	{
		swappedIn = null;
		policy.reset();
		PeriodicThread.reset();
	}

	// Find the next thread to schedule and run one timeslice. The return
	// value indicates whether we ran something (true) or are idle (false,
	// may be due to a time step, including a swap delay).

	@Override
	public boolean reschedule()
	{
		if (swappedIn != null &&
			swappedIn.getRunState() == RunState.TIMESTEP)
		{
			return false;	// Can't schedule anything else and we're idle.
		}

		if (policy.reschedule())
		{
			ISchedulableThread best = policy.getThread();

			if (swappedIn != best)
			{
				if (swappedIn != null)
				{
					RTLogger.log(new RTThreadSwapMessage(SwapType.Out,swappedIn, this, 0,0));
				}

				long delay = SystemClock.getWallTime() - best.getSwapInBy();

				if (best.getSwapInBy() > 0 && delay > 0)
				{
					RTLogger.log(new RTThreadSwapMessage(SwapType.DelayedIn,best, this, 0,delay));
				}
				else
				{
					RTLogger.log(new RTThreadSwapMessage(SwapType.In,best, this, 0,0));
				}
			}

			swappedIn = best;
			swappedIn.runslice(policy.getTimeslice());

			switch (swappedIn.getRunState())
			{
				case COMPLETE:
					RTLogger.log(new RTThreadSwapMessage(SwapType.Out,swappedIn, this, 0,0));

        			RTLogger.log(new RTThreadKillMessage(swappedIn,this));

        			swappedIn = null;
        			return true;	// We may be able to run other threads

				case TIMESTEP:
					return false;	// We're definitely idle

				default:
					return true;	// We may be able to run other threads
			}
		}
		else
		{
			return false;
		}
	}

	@Override
	public long getMinimumTimestep()
	{
		if (swappedIn == null)
		{
			return Long.MAX_VALUE;		// We're not in timestep
		}
		else
		{
			switch (swappedIn.getRunState())
			{
				case TIMESTEP:
					return swappedIn.getTimestep();

				case RUNNING:
					return -1;			// Can't timestep

				default:
					return Long.MAX_VALUE;
			}
		}
	}

	public void createThread(ISchedulableThread th)
	{
		RTLogger.log(new RTThreadCreateMessage(th,this));
	}

	public void deploy(ObjectValue object)
	{
		RTLogger.log(new RTDeployObjectMessage(object,this));
	}

	public long getCyclesDuration(long cycles)
	{
		//TODO optimize by converting the speed into the correct units only once
		return SystemClock.timeToInternal(TimeUnit.seconds,new Double(cycles)/clock); // cycles/cpu [Hz]
	}

	public int getNumber()
	{
		return cpuNumber;
	}

	@Override
	public boolean isVirtual()
	{
		return this == vCPU;
	}

	@Override
	public String getStatus()
	{
		return policy.getStatus();
	}
}
