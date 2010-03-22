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

import java.io.Serializable;

abstract public class Resource implements Serializable
{
    private static final long serialVersionUID = 1L;
	public final SchedulingPolicy policy;
	protected String name = "unknown";
	protected ResourceScheduler scheduler;

	public Resource(SchedulingPolicy policy)
	{
		this.policy = policy;
	}

	public void setScheduler(ResourceScheduler scheduler)
	{
		this.scheduler = scheduler;
	}

	public ResourceScheduler getScheduler()
	{
		return scheduler;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getName()
	{
		return name;
	}

	@Override
	public String toString()
	{
		return name;
	}

	@Override
	public boolean equals(Object other)
	{
		if (other instanceof Resource)
		{
			Resource ro = (Resource)other;
			return name.equals(ro.name);
		}

		return false;
	}

	@Override
	public int hashCode()
	{
		return name.hashCode();
	}

	public void register(SchedulableThread th, long priority)
	{
		policy.register(th, priority);
	}

	public void unregister(SchedulableThread th)
	{
		policy.unregister(th);
	}

	// Find the next thread to schedule and run one timeslice. The return
	// value indicates whether we ran something (true) or are idle (false,
	// which may be due to a time step).

	abstract public boolean reschedule();

	// Get the minimum duration for a timestep, or -1 if can't, or Long.MAX_VALUE
	// if no one is at a timestep.

	abstract public long getMinimumTimestep();

	public void advance()
	{
		policy.advance();
	}

	public boolean hasActive()
	{
		return policy.hasActive();
	}

	abstract public void reset();

	abstract public String getStatus();
}
