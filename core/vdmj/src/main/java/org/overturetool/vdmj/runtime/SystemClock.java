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

import org.overturetool.vdmj.messages.RTLogger;
import org.overturetool.vdmj.values.CPUValue;

public class SystemClock
{
	private static long wallTime = 0;
	private static long minStepTime = Long.MAX_VALUE;

	public static void reset()
	{
		wallTime = 0;
		minStepTime = Long.MAX_VALUE;
	}

	public static synchronized long getWallTime()
	{
		return wallTime;
	}

	public static synchronized void timeStep(long cpuMin)
	{
		if (cpuMin < minStepTime)
		{
			minStepTime = cpuMin;
		}

		boolean canStep = true;

		for (CPUValue cpu: CPUValue.allCPUs)
		{
			if (!cpu.canTimeStep())
			{
				canStep = false;
			}
		}

		if (canStep)
		{
			for (CPUValue cpu: CPUValue.allCPUs)
			{
				cpu.timeStep(minStepTime);
			}

			wallTime += minStepTime;
			RTLogger.diag("TIMESTEP = " + minStepTime + ", now = " + wallTime);

			minStepTime = Long.MAX_VALUE;
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
