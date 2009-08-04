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

import java.util.HashMap;
import java.util.Map;

public class FPPolicy extends FCFSPolicy
{
	private final Map<Thread, Long> priorities;

	public FPPolicy()
	{
		this.priorities = new HashMap<Thread, Long>();
	}

	@Override
	public synchronized void addThread(Thread thread, long priority)
	{
		super.addThread(thread, priority);
		priorities.put(thread, priority);
	}

	@Override
	public long getTimeslice()
	{
		return priorities.get(bestThread);
	}
}
