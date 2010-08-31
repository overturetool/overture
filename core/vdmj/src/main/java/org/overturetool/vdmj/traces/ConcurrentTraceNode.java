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

package org.overturetool.vdmj.traces;

import java.util.List;
import java.util.Vector;

public class ConcurrentTraceNode extends TraceNode
{
	public List<TraceNode> nodes;

	public ConcurrentTraceNode()
	{
		nodes = new Vector<TraceNode>();
	}

	@Override
	public TestSequence getTests()
	{
		List<TestSequence> nodetests = new Vector<TestSequence>();
		int count = nodes.size();

		for (TraceNode node: nodes)
		{
			nodetests.add(node.getTests());
		}

		TestSequence tests = new TestSequence();
		PermuteArray pa = new PermuteArray(count);

		while (pa.hasNext())
		{
			int[] sizes = new int[count];
			int[] perm = pa.next();

			for (int i=0; i<count; i++)
			{
				sizes[i] = nodetests.get(perm[i]).size();
			}

			Permutor p = new Permutor(sizes);

    		while (p.hasNext())
    		{
    			int[] select = p.next();
    			CallSequence seq = getVariables();

    			for (int i=0; i<count; i++)
    			{
    				CallSequence subseq = nodetests.get(perm[i]).get(select[i]);
    				seq.addAll(subseq);
    			}

    			tests.add(seq);
    		}
		}

		return tests;
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("|| (");
		String sep = "";

		for (TraceNode node: nodes)
		{
			sb.append(sep);
			sb.append(node.toString());
			sep = ", ";
		}

		sb.append(")");
		return sb.toString();
	}
}
