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

import java.util.HashMap;
import java.util.Map;

import org.overture.codegen.runtime.Utils;

public class RepeatTraceNode extends TraceNode implements IIterableTraceNode
{
	public final int from;
	public final int to;

	private Map<Integer, Pair<Integer, Integer>> indics;
	private final TraceNode repeat;

	public RepeatTraceNode(TraceNode repeat, long from, long to)
	{
		this.repeat = repeat;
		this.from = (int) from;
		this.to = (int) to;
	}

	/*
	 * (non-Javadoc)
	 * @see org.overture.interpreter.traces.IIterableTraceNode#get(int)
	 */
	@Override
	public CallSequence get(int index)
	{
		if (indics == null)
		{
			size();
		}
		Pair<Integer, Integer> v = indics.get(index);

		int r = v.getFirst();

		if (r == 0)
		{
			CallSequence seq = getVars();
			CallStatement skip = new CallStatement()
			{
				public Object execute()
				{
					return Utils.VOID_VALUE;
				}

				@Override
				public String toString()
				{
					return "skip";
				}
			};

			seq.add(skip);
			return seq;

		} else
		{

			TestSequence rtests = repeat.getTests();
			int count = rtests.size();
			int[] c = new int[r];

			for (int i = 0; i < r; i++)
			{
				c[i] = count;
			}

			Permutor p = new Permutor(c);
			int[] select = null;
			for (int i = 0; i < v.getSecond(); i++)
			{
				select = p.next();
			}

			CallSequence seq = getVars();

			for (int i = 0; i < r; i++)
			{
				seq.addAll(rtests.get(select[i]));
			}
			return seq;
		}

	}

	@Override
	public TestSequence getTests()
	{
		return new LazyTestSequence(this);
	}

	/*
	 * (non-Javadoc)
	 * @see org.overture.interpreter.traces.IIterableTraceNode#size()
	 */
	@Override
	public int size()
	{
		if (indics != null)
		{
			return indics.size();
		}

		indics = new HashMap<Integer, Pair<Integer, Integer>>();

		int size = 0;
		TestSequence rtests = repeat.getTests();
		int count = rtests.size();
		for (int r = from; r <= to; r++)
		{
			if (r == 0)
			{

				indics.put(size, new Pair<Integer, Integer>(r, 0));
				size++;
				continue;
			}

			int[] c = new int[r];

			for (int i = 0; i < r; i++)
			{
				c[i] = count;
			}

			Permutor p = new Permutor(c);

			int j = 0;
			while (p.hasNext())
			{
				j++;
				p.next();
				indics.put(size, new Pair<Integer, Integer>(r, j));
				size++;
			}
		}
		return size;
	}

	@Override
	public String toString()
	{
		return repeat.toString() + (from == 1 && to == 1 ? ""
				: from == to ? "{" + from + "}" : "{" + from + ", " + to + "}");
	}
}
