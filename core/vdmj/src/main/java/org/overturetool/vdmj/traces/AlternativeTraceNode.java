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

public class AlternativeTraceNode extends TraceNode
{
	public List<TraceNode> alternatives;

	public AlternativeTraceNode()
	{
		this.alternatives = new Vector<TraceNode>();
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		String sep = "";

		for (TraceNode node: alternatives)
		{
			sb.append(sep);
			sb.append(node.toString());
			sep = " | ";
		}

		sb.append(")");
		return sb.toString();
	}

	@Override
	public TestSequence getTests()
	{
		TestSequence tests = new TestSequence();

		for (TraceNode node: alternatives)
		{
			// Alternatives within an alternative are just like larger alts,
			// so we add all the lower alts to the list...

    		for (CallSequence test: node.getTests())
    		{
    			CallSequence seq = getVariables();
    			seq.addAll(test);
    			tests.add(seq);
    		}
		}

		return tests;
	}
}
