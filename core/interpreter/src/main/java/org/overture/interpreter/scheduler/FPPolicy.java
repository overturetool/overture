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

import java.util.HashMap;
import java.util.Map;

import org.overture.parser.config.Properties;

public class FPPolicy extends FCFSPolicy
{
	private static final long serialVersionUID = 1L;
	private final Map<ISchedulableThread, Long> priorities;

	public FPPolicy()
	{
		this.priorities = new HashMap<>();
	}

	@Override
	public void reset()
	{
		super.reset();
		priorities.clear();
	}

	@Override
	public synchronized void register(ISchedulableThread thread, long priority)
	{
		super.register(thread, priority);

		priorities.put(thread, priority == 0 ? Properties.scheduler_fcfs_timeslice
				: thread.isVirtual() ? Properties.scheduler_virtual_timeslice
						: priority);
	}

	@Override
	public long getTimeslice()
	{
		long slice = priorities.get(bestThread);

		if (Properties.scheduler_jitter > 0)
		{
			// Plus or minus jitter ticks...
			slice += PRNG.nextLong() % (Properties.scheduler_jitter + 1);
		}

		return slice;
	}

	@Override
	public boolean hasPriorities()
	{
		return true;
	}
}
