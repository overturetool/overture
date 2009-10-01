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

import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.statements.SkipStatement;

public class RepeatTraceNode extends TraceNode
{
	public final TraceNode repeat;
	public final int from;
	public final int to;

	public RepeatTraceNode(TraceNode repeat, long from, long to)
	{
		this.repeat = repeat;
		this.from = (int)from;
		this.to = (int)to;
	}

	@Override
	public String toString()
	{
		return repeat.toString() +
    		((from == 1 && to == 1) ? "" :
    			(from == to) ? ("{" + from + "}") :
    				("{" + from + ", " + to + "}"));
	}

	@Override
	public TestSequence getTests()
	{
		TestSequence tests = new TestSequence();
		TestSequence rtests = repeat.getTests();
		int count = rtests.size();

		for (int r = from; r <= to; r++)
		{
			if (r == 0)
			{
				CallSequence seq = getVariables();
   				seq.add(new SkipStatement(new LexLocation()));
    			tests.add(seq);
				continue;
			}

 			int[] c = new int[r];

			for (int i=0; i<r; i++)
			{
				c[i] = count;
			}

			Permutor p = new Permutor(c);

			while (p.hasNext())
			{
	   			CallSequence seq = getVariables();
	   			int[] select = p.next();

	   			for (int i=0; i<r; i++)
    			{
    				seq.addAll(rtests.get(select[i]));
    				// seq.addHashes(rtests.get(select[i]).getHashes());
    			}

    			tests.add(seq);
			}
		}

		return tests;
	}
}
