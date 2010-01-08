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
import java.util.ListIterator;
import java.util.Random;
import java.util.Vector;

import org.overturetool.vdmj.definitions.ClassDefinition;
import org.overturetool.vdmj.messages.InternalException;
import org.overturetool.vdmj.runtime.ClassInterpreter;
import org.overturetool.vdmj.runtime.Interpreter;
import org.overturetool.vdmj.statements.Statement;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.FlatEnvironment;
import org.overturetool.vdmj.typechecker.PrivateClassEnvironment;
import org.overturetool.vdmj.util.Utils;

@SuppressWarnings("serial")
public class TestSequence extends Vector<CallSequence>
{
	@Override
	public String toString()
	{
		return Utils.listToString(this, "\n");
	}

	/**
	 * Filter remaining tests based on one set of results. The result list
	 * passed in is the result of running the test, which is the n'th test
	 * in the TestSequence (from 1 - size).
	 */

	public void filter(List<Object> result, CallSequence test, int n)
	{
		if (result.get(result.size()-1) == Verdict.FAILED)
		{
			int stem = result.size() - 1;
			ListIterator<CallSequence> it = listIterator(n);

			while (it.hasNext())
			{
				CallSequence other = it.next();

				if (other.compareStem(test, stem))
				{
					other.setFilter(n);
				}
			}
		}
	}

	public void typeCheck(ClassDefinition classdef)
	{
		Interpreter interpreter = Interpreter.getInstance();
		Environment env = null;

		if (interpreter instanceof ClassInterpreter)
		{
			 env = new FlatEnvironment(
				classdef.getSelfDefinition(),
				new PrivateClassEnvironment(classdef, interpreter.getGlobalEnvironment()));
		}
		else
		{
			 env = interpreter.getGlobalEnvironment();
		}

		for (CallSequence test: this)
		{
    		for (Statement statement: test)
    		{
				try
				{
					interpreter.typeCheck(statement, env);
				}
				catch (Exception e)
				{
					// "Better to have tried and failed than never to have
					// tried at all" :-)
				}
    		}
		}
	}

	public void reduce(float subset, TraceReductionType reduction)
    {
		switch (reduction)
		{
			case NONE:
				break;

			case RANDOM:
				randomReduction(subset);
				break;

			case SHAPES:
				shapesReduction(subset);
				break;

			default:
				throw new InternalException(53, "Unknown trace reduction");
		}
    }

	private void shapesReduction(float subset)
    {
		// TBS
    }

	private void randomReduction(float subset)
    {
		int s = size();
		long n = Math.round(Math.ceil(s * subset));
		Random prng = new Random();

		if (n < s)
		{
			long delta = s - n;

			for (long i=0; i<delta; i++)
			{
				int x = prng.nextInt(s);
				this.remove(x);
				s--;
			}
		}
    }
}