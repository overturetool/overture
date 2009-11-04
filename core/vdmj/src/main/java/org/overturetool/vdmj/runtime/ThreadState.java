/*******************************************************************************
 *
 *	Copyright (C) 2008 Fujitsu Services Ltd.
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

import org.overturetool.vdmj.debug.DBGPReader;
import org.overturetool.vdmj.values.CPUValue;

/**
 * A class to hold some runtime information for each VDM thread.
 */

public class ThreadState
{
	public final long threadId;
	public final DBGPReader dbgp;
	public final CPUValue CPU;

	private long timesliceLeft = 0;
	private boolean atomic = false;		// don't reschedule

	public InterruptAction action;
	public int stepline;
	public RootContext nextctxt;
	public Context outctxt;

	private long timestep;		// Current step being made (not wall time)

	public ThreadState(DBGPReader dbgp, CPUValue cpu)
	{
		this.dbgp = dbgp;
		this.threadId = Thread.currentThread().getId();
		this.CPU = cpu;
		init();
	}

	public void init()
	{
		this.action = InterruptAction.RUNNING;
		this.setTimestep(-1);
		set(0, null, null);
	}

	public synchronized void set(
		int stepline, RootContext nextctxt, Context outctxt)
	{
		this.stepline = stepline;
		this.nextctxt = nextctxt;
		this.outctxt = outctxt;
	}

	public synchronized void setTimestep(long timestep)
	{
		this.timestep = timestep;
	}

	public synchronized long getTimestep()
	{
		return timestep;
	}

	public void reschedule()	// Called for VDM_RT at every statement/expression
	{
		if (CPUValue.stopping)
		{
			// We can't be the main thread, as that is stopping anyway
			throw new RTException("CPU Stopping");
		}

		if (!atomic && --timesliceLeft < 0)
		{
			timesliceLeft = CPU.reschedule();
		}
	}

	public synchronized void setAtomic(boolean atomic)
	{
		this.atomic = atomic;
	}
}
