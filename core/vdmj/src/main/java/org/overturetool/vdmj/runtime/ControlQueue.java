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

import java.util.LinkedList;
import java.util.List;

import org.overturetool.vdmj.values.CPUValue;

public class ControlQueue
{
	private CPUThread joined = null;
	private boolean stimmed = false;
	private List<CPUThread> waiters = new LinkedList<CPUThread>();

	public void join(CPUValue cpu)
	{
		CPUThread self = new CPUThread(cpu);

		if (joined != null)
		{
			waiters.add(self);
			cpu.yield(RunState.WAITING);
		}

		synchronized (this)
		{
			joined = self;
		}
	}

	public void block()
	{
		if (stimmed)
		{
			synchronized (this)
			{
				stimmed = false;
			}
		}
		else
		{
			joined.cpu.yield(RunState.WAITING);
		}
	}

	public void stim()
	{
		synchronized (this)
		{
			stimmed = true;
		}

		if (joined != null)
		{
			joined.setState(RunState.RUNNABLE);
			joined.cpu.wakeUp();
		}
	}

	public void leave()
	{
		synchronized (this)
		{
			joined = null;
		}

		if (!waiters.isEmpty())
		{
			CPUThread w = waiters.remove(0);
			w.setState(RunState.RUNNABLE);
			w.cpu.wakeUp();
		}
	}
}
