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

import java.util.List;
import java.util.Vector;

import org.overturetool.vdmj.runtime.Context;

public class SequenceTraceNode extends TraceNode
{
	public List<TraceNode> nodes;

	public SequenceTraceNode(Context ctxt)
	{
		super(ctxt);
		this.nodes = new Vector<TraceNode>();
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		String sep = "";

		for (TraceNode node: nodes)
		{
			sb.append(sep);
			sb.append(node.toString());
			sep = "; ";
		}

		return sb.toString();
	}

	@Override
	public TestSequence getTests()
	{
		List<TestSequence> nodetests = new Vector<TestSequence>();
		int count = nodes.size();
		int[] sizes = new int[count];
		int n = 0;

		for (TraceNode node: nodes)
		{
			TestSequence nt = node.getTests();
			nodetests.add(nt);
			sizes[n++] = nt.size();
		}

		TestSequence tests = new TestSequence();
		Permutor p = new Permutor(sizes);

		while (p.hasNext())
		{
			int[] select = p.next();
			CallSequence seq = new CallSequence();

			for (int i=0; i<count; i++)
			{
				CallSequence subseq = nodetests.get(i).get(select[i]);
				seq.addAll(subseq);
				seq.setContext(subseq.ctxt);
			}

			tests.add(seq);
		}

		return tests;
	}
}
