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

import java.io.Serializable;

import org.overturetool.vdmj.scheduler.SchedulingPolicy;

abstract public class SchedulingPolicy implements Serializable
{
    private static final long serialVersionUID = 1L;

	abstract public boolean reschedule();
	abstract public SchedulableThread getThread();
	abstract public long getTimeslice();
	abstract public void register(SchedulableThread thread, long priority);
	abstract public void unregister(SchedulableThread thread);
	abstract public void reset();
	abstract public void advance();
	abstract public boolean hasActive();
	abstract public boolean hasPriorities();
	abstract public String getStatus();

	public static SchedulingPolicy factory(String type)
	{
		if (type.equals("FP"))
		{
			return new FPPolicy();
		}

		return new FCFSPolicy();		// Default for everything!
	}
}
