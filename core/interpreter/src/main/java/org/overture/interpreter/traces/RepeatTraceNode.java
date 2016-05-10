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

package org.overture.interpreter.traces;

import java.util.HashMap;
import java.util.Map;

import org.overture.ast.factory.AstFactory;
import org.overture.ast.lex.LexLocation;
import org.overture.interpreter.traces.util.LazyTestSequence;
import org.overture.interpreter.traces.util.Pair;

public class RepeatTraceNode extends TraceNode implements IIterableTraceNode
{
	public final int from;
	private Map<Integer, Pair<Integer, Integer>> indics;
	public final TraceNode repeat;

	public final int to;

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

		int r = v.first;

		if (r == 0)
		{
			CallSequence seq = getVariables();
			seq.add(AstFactory.newASkipStm(new LexLocation()));
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
			for (int i = 0; i < v.second; i++)
			{
				select = p.next();
			}

			CallSequence seq = getVariables();

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

		indics = new HashMap<>();

		int size = 0;
		TestSequence rtests = repeat.getTests();
		int count = rtests.size();
		for (int r = from; r <= to; r++)
		{
			if (r == 0)
			{

				indics.put(size, new Pair<>(r, 0));
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
				indics.put(size, new Pair<>(r, j));
				size++;
			}
		}
		return size;
	}

	@Override
	public String toString()
	{
		return repeat.toString()
				+ (from == 1 && to == 1 ? "" : from == to ? "{" + from + "}"
						: "{" + from + ", " + to + "}");
	}
}
