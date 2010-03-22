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

import org.overturetool.vdmj.scheduler.AsyncThread;
import org.overturetool.vdmj.values.ObjectValue;

public class ThreadObjectMap extends HashMap<Thread, ObjectValue>
{
	private static final long serialVersionUID = 1L;

	@Override
	public synchronized ObjectValue get(Object key)
	{
		return super.get(key);
	}

	@Override
	public synchronized ObjectValue put(Thread key, ObjectValue value)
	{
		return super.put(key, value);
	}

	@Override
	public synchronized void clear()
	{
		super.clear();
	}

	@Override
	public synchronized ObjectValue remove(Object key)
	{
		return super.remove(key);
	}

	public synchronized void abort()
	{
		for (Thread th: keySet())
		{
			if (th instanceof AsyncThread)		// Don't interrupt main
			{
				th.interrupt();
			}
		}
	}
}
