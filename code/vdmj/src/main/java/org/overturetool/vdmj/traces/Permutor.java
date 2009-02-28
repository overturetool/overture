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

package org.overturetool.vdmj.traces;

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
	}

	private void permute()
	{
		for (int i=0; i<count; i++)
		{
			if (++current[i] < limit[i])
			{
				done = false;
				break;
			}

			current[i] = 0;

			if (i == count-1)
			{
				done = true;
			}
		}
	}

	public int[] next()
	{
		return current;
	}

	public boolean hasNext()
	{
		if (!done)
		{
       		permute();
       		return true;
		}

		return false;
	}

	public static void main(String[] args)
	{
		int[] a = {1,1,1,1,1};
		Permutor p = new Permutor(a);

		while (p.hasNext())
		{
			for (int i: p.next())
			{
				System.out.print(i);
				System.out.print(" ");
			}

			System.out.println();
		}
	}
}
