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

package org.overture.codegen.runtime.traces;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class ConcurrentTraceNode extends TraceNode implements IIterableTraceNode
{
	private Map<Integer, Pair<Integer, Integer>> indics;
	public List<TraceNode> nodes;

	public void add(TraceNode node)
	{
		nodes.add(node);
	}

	public ConcurrentTraceNode()
	{
		nodes = new Vector<TraceNode>();
	}

	@Override
	public CallSequence get(int index)
	{
		if (indics == null)
		{
			size();
		}
		Pair<Integer, Integer> v = indics.get(index);

		List<TestSequence> nodetests = new Vector<TestSequence>();
		int count = nodes.size();

		for (TraceNode node : nodes)
		{
			nodetests.add(node.getTests());
		}

		PermuteArray pa = new PermuteArray(count);

		int r = 0;

		while (pa.hasNext())
		{
			int[] perm = pa.next();
			r++;
			if (r < v.getFirst())
			{
				continue;
			}

			int[] sizes = new int[count];

			for (int i = 0; i < count; i++)
			{
				sizes[i] = nodetests.get(perm[i]).size();
			}

			Permutor p = new Permutor(sizes);

			int j = 0;
			while (p.hasNext())
			{
				int[] select = p.next();
				j++;
				if (j < v.getSecond())
				{
					continue;
				}
				CallSequence seq = getVars();

				for (int i = 0; i < count; i++)
				{
					TestSequence ith = nodetests.get(perm[i]);

					if (!ith.isEmpty())
					{
						CallSequence subseq = ith.get(select[i]);
						seq.addAll(subseq);
					}
				}

				return seq;
			}
		}

		return null;
	}

	@Override
	public TestSequence getTests()
	{
		return new LazyTestSequence(this);
	}

	@Override
	public int size()
	{
		if (indics != null)
		{
			return indics.size();
		}

		indics = new HashMap<Integer, Pair<Integer, Integer>>();

		int size = 0;

		List<TestSequence> nodetests = new Vector<TestSequence>();
		int count = nodes.size();

		for (TraceNode node : nodes)
		{
			nodetests.add(node.getTests());
		}

		PermuteArray pa = new PermuteArray(count);

		int r = 0;
		while (pa.hasNext())
		{
			r++;
			int[] sizes = new int[count];
			int[] perm = pa.next();

			for (int i = 0; i < count; i++)
			{
				sizes[i] = nodetests.get(perm[i]).size();
			}

			Permutor p = new Permutor(sizes);

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
		StringBuilder sb = new StringBuilder();
		sb.append("|| (");
		String sep = "";

		for (TraceNode node : nodes)
		{
			sb.append(sep);
			sb.append(node.toString());
			sep = ", ";
		}

		sb.append(")");
		return sb.toString();
	}
}
