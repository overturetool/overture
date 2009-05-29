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

import org.overturetool.vdmj.runtime.Context;

public class RepeatTraceNode extends TraceNode
{
	public final TraceNode repeat;
	public final long from;
	public final long to;

	public RepeatTraceNode(Context ctxt, TraceNode repeat, long from, long to)
	{
		super(ctxt);
		this.repeat = repeat;
		this.from = from;
		this.to = to;
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

		for (CallSequence test: rtests)
		{
    		for (long r = from; r <= to; r++)
    		{
    			CallSequence seq = new CallSequence();

    			for (int i=0; i<r; i++)
    			{
    				seq.addAll(test);
    			}

    			seq.setContext(test.ctxt);
    			tests.add(seq);
    		}
		}

		return tests;
	}
}
