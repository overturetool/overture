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

import java.util.BitSet;

import org.overturetool.vdmj.values.TransactionValue;

public class SystemClock
{
	private static long wallTime = 0;
	private static long minStepTime = Long.MAX_VALUE;
	private static BitSet runningCPUs = new BitSet();

	public static void init()
	{
		wallTime = 0;
		reset();
	}

	public static void reset()
	{
		runningCPUs.clear();
		minStepTime = Long.MAX_VALUE;
	}

	public static synchronized void cpuRunning(int cpu, boolean running)
	{
		runningCPUs.set(cpu, running);
		if (!running) unblock();
	}

	public static synchronized long getWallTime()
	{
		return wallTime;
	}

	@SuppressWarnings("unused")
	public static synchronized void timeStep(int cpu, long step)
	{
		if (step < minStepTime)
		{
			minStepTime = step;
		}

		// RTLogger.log("CPU " + cpu + " ready to TIMESTEP by " + step);

		if (step == 0)
		{
			TransactionValue.commitOne(Thread.currentThread().getId());
			unblock();
		}
		else if (runningCPUs.cardinality() == 0)
		{
			wallTime += minStepTime;
			// RTLogger.log("TIMESTEP = " + minStepTime + ", now = " + wallTime);
			TransactionValue.commitAll();

			minStepTime = Long.MAX_VALUE;
			runningCPUs.clear();
			unblock();
		}
		else
		{
			block();
		}
	}

	private static synchronized void unblock()
	{
		SystemClock.class.notifyAll();
	}

	private static void block()
	{
		while (true)
		{
			try
			{
				SystemClock.class.wait();
				break;
			}
			catch (InterruptedException e)
			{
				throw new RTException("Thread stopped");
			}
		}
	}
}
