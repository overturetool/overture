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

public class AlternativeTraceNode extends TraceNode
		implements IIterableTraceNode
{
	public List<TraceNode> alternatives;

	public void add(TraceNode node)
	{
		alternatives.add(node);
	}

	private Map<Integer, Pair<Integer, Integer>> indics;

	public AlternativeTraceNode()
	{
		this.alternatives = new Vector<TraceNode>();
	}

	@Override
	public CallSequence get(int index)
	{
		if (indics == null)
		{
			size();
		}

		Pair<Integer, Integer> v = indics.get(index);

		TraceNode tmp = alternatives.get(v.getFirst());

		if (tmp instanceof IIterableTraceNode)
		{
			IIterableTraceNode in = (IIterableTraceNode) tmp;

			CallSequence callSeq = tmp.getVars();
			callSeq.addAll(in.get(v.getSecond()));

			return callSeq;
		} else
		{
			CallSequence callSeq = tmp.getVars();
			callSeq.addAll(tmp.getTests().get(v.getSecond()));

			return callSeq;
		}
	}

	@Override
	public TestSequence getTests()
	{
		return new LazyTestSequence(this);
	}

	@Override
	public int size()
	{
		int size = 0;

		if (indics != null)
		{
			return indics.size();
		}

		indics = new HashMap<Integer, Pair<Integer, Integer>>();
		int k = 0;

		for (TraceNode node : alternatives)
		{
			// Alternatives within an alternative are just like larger alts,
			// so we add all the lower alts to the list...

			int s = 0;
			if (node instanceof IIterableTraceNode)
			{
				s = ((IIterableTraceNode) node).size();
			} else
			{
				s = node.getTests().size();
			}

			for (int i = 0; i < s; i++)
			{
				indics.put(size + i, new Pair<Integer, Integer>(k, i));
			}

			size += s;
			k++;
		}
		return size;
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		String sep = "";

		for (TraceNode node : alternatives)
		{
			sb.append(sep);
			sb.append(node.toString());
			sep = " | ";
		}

		sb.append(")");
		return sb.toString();
	}
}
