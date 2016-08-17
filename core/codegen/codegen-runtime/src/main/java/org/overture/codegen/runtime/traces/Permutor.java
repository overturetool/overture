/*******************************************************************************
 *
 *	Copyright (C) 2008, 2009 Fujitsu Services Ltd.
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

package org.overture.codegen.runtime.traces;

public class Permutor
{
	private final int[] limit;
	private final int count;
	private final int[] current;

	private boolean done = false;

	public Permutor(int[] limits)
	{
		this.limit = limits;
		this.count = limits.length;
		this.current = new int[count];
		this.done = true;

		for (int i = 0; i < count; i++)
		{
			if (limits[i] > 0)
			{
				this.done = false;
				break;
			}
		}
	}

	private int[] permute()
	{
		int[] old = new int[count];
		System.arraycopy(current, 0, old, 0, count);

		for (int i = 0; i < count; i++)
		{
			if (++current[i] < limit[i])
			{
				done = false;
				break;
			}

			current[i] = 0;

			if (i == count - 1)
			{
				done = true;
			}
		}

		return old;
	}

	public int[] next()
	{
		return permute();
	}

	public boolean hasNext()
	{
		return !done;
	}
}
