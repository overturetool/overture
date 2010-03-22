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

import org.overturetool.vdmj.messages.RTLogger;
import org.overturetool.vdmj.values.ObjectValue;

public class CPUResource extends Resource
{
    private static final long serialVersionUID = 1L;
	private static int nextCPU = 0;
	public static CPUResource vCPU = null;

	private final int cpuNumber;
	private final double clock;

	private SchedulableThread running = null;

	public CPUResource(SchedulingPolicy policy, double clock)
	{
		super(policy);
		this.cpuNumber = nextCPU++;
		this.clock = clock;

		running = null;

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
		running = null;
		policy.reset();
		PeriodicThread.reset();
	}

	// Find the next thread to schedule and run one timeslice. The return
	// value indicates whether we ran something (true) or are idle (false,
	// may be due to a time step, including a swap delay).

	@Override
	public boolean reschedule()
	{
		if (policy.reschedule())
		{
			SchedulableThread best = policy.getThread();

			if (running != best)
			{
				if (running != null)
				{
	    			RTLogger.log(
	    				"ThreadSwapOut -> id: " + running.getId() +
	    				objRefString(running.getObject()) +
	    				" cpunm: " + cpuNumber +
	    				" overhead: " + 0);
				}

				long delay = SystemClock.getWallTime() - best.getSwapInBy();

				if (best.getSwapInBy() > 0 && delay > 0)
				{
		        	RTLogger.log(
		        		"DelayedThreadSwapIn -> id: " + best.getId() +
		        		objRefString(best.getObject()) +
		        		" delay: " + delay +
		        		" cpunm: " + cpuNumber +
		        		" overhead: " + 0);
				}
				else
				{
    				RTLogger.log(
    					"ThreadSwapIn -> id: " + best.getId() +
    					objRefString(best.getObject()) +
    					" cpunm: " + cpuNumber +
    					" overhead: " + 0);
				}
			}

			running = best;
			running.runslice(policy.getTimeslice());

			if (running.getRunState() == RunState.COMPLETE)
			{
    			RTLogger.log(
    				"ThreadSwapOut -> id: " + running.getId() +
    				objRefString(running.getObject()) +
    				" cpunm: " + cpuNumber +
    				" overhead: " + 0);

    			RTLogger.log(
					"ThreadKill -> id: " + running.getId() +
					" cpunm: " + cpuNumber);

    			running = null;
			}

			return true;
		}
		else
		{
			return false;
		}
	}

	@Override
	public long getMinimumTimestep()
	{
		return policy.getTimestep();
	}

	public void createThread(SchedulableThread th)
	{
		RTLogger.log(
			"ThreadCreate -> id: " + th.getId() +
			" period: " + th.isPeriodic() +
			objRefString(th.getObject()) +
			" cpunm: " + cpuNumber);
	}

	public void deploy(ObjectValue object)
	{
		RTLogger.log(
			"DeployObj -> objref: " + object.objectReference +
			" clnm: \"" + object.type + "\"" +
			" cpunm: " + cpuNumber);
	}

	private String objRefString(ObjectValue obj)
	{
		return
			" objref: " + (obj == null ? "nil" : obj.objectReference) +
			" clnm: " + (obj == null ? "nil" : ("\"" + obj.type + "\""));
	}

	public long getCyclesDuration(long cycles)
	{
		return (long)(cycles/clock + 1);		// Same as VDMTools
	}

	public int getNumber()
	{
		return cpuNumber;
	}

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
