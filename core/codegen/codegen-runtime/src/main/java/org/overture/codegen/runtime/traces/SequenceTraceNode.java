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
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class SequenceTraceNode extends TraceNode implements IIterableTraceNode
{
	private Map<Integer, Integer[]> indics;

	public List<TraceNode> nodes;

	public void add(TraceNode node)
	{
		nodes.add(node);
	}

	public SequenceTraceNode()
	{
		this.nodes = new Vector<TraceNode>();
	}

	@Override
	public CallSequence get(int index)
	{
		if (indics == null)
		{
			size();
		}
		Integer[] select = indics.get(index);

		CallSequence seq = getVars();

		List<TestSequence> nodetests = new Vector<TestSequence>();
		int count = nodes.size();

		for (TraceNode node : nodes)
		{
			TestSequence nt = node.getTests();
			nodetests.add(nt);
		}

		for (int i = 0; i < count; i++)
		{
			TestSequence ith = nodetests.get(i);

			if (!ith.isEmpty())
			{
				CallSequence subseq = ith.get(select[i]);
				seq.addAll(subseq);
			}
		}
		return seq;
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

		indics = new HashMap<Integer, Integer[]>();

		List<TestSequence> nodetests = new Vector<TestSequence>();
		int count = nodes.size();
		int[] sizes = new int[count];
		int n = 0;

		for (TraceNode node : nodes)
		{
			TestSequence nt = node.getTests();
			nodetests.add(nt);
			sizes[n++] = nt.size();
		}

		int size = 0;

		Permutor p = new Permutor(sizes);

		while (p.hasNext())
		{
			int[] next = p.next();

			Integer[] select = new Integer[next.length];
			for (int i = 0; i < next.length; i++)
			{
				select[i] = next[i];
			}
			indics.put(size, select);
			size++;
		}

		return size;
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		String sep = "";

		for (TraceNode node : nodes)
		{
			sb.append(sep);
			sb.append(node.toString());
			sep = "; ";
		}

		return sb.toString();
	}
}
