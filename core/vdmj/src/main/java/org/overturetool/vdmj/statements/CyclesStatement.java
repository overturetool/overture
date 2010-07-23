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

package org.overturetool.vdmj.statements;

import org.overturetool.vdmj.expressions.Expression;
import org.overturetool.vdmj.expressions.IntegerLiteralExpression;
import org.overturetool.vdmj.expressions.RealLiteralExpression;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.scheduler.BasicSchedulableThread;
import org.overturetool.vdmj.scheduler.ISchedulableThread;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.values.Value;

public class CyclesStatement extends Statement
{
	private static final long serialVersionUID = 1L;
	public final Expression cycles;
	public final Statement statement;

	private long value = 0;

	public CyclesStatement(
		LexLocation location, Expression cycles, Statement stmt)
	{
		super(location);
		this.cycles = cycles;
		this.statement = stmt;
	}

	@Override
	public String kind()
	{
		return "cycles";
	}

	@Override
	public String toString()
	{
		return "cycles (" + cycles + ") " + statement;
	}

	@Override
	public Type typeCheck(Environment env, NameScope scope)
	{
		if (cycles instanceof IntegerLiteralExpression)
		{
			IntegerLiteralExpression i = (IntegerLiteralExpression)cycles;

			if (i.value.value < 0)
			{
				cycles.report(3282, "Argument to cycles must be integer >= 0");
			}

			value = i.value.value;
		}
		else if (cycles instanceof RealLiteralExpression)
		{
			RealLiteralExpression i = (RealLiteralExpression)cycles;

			if (i.value.value < 0 ||
				Math.floor(i.value.value) != i.value.value)
			{
				cycles.report(3282, "Argument to cycles must be integer >= 0");
			}

			value = (long)i.value.value;
		}
		else
		{
			cycles.report(3282, "Argument to cycles must be integer >= 0");
		}

		return statement.typeCheck(env, scope);
	}

	@Override
	public Value eval(Context ctxt)
	{
		location.hit();
		cycles.location.hit();

		ISchedulableThread me = BasicSchedulableThread.getThread(Thread.currentThread());

		if (me.inOuterTimestep())
		{
			// Already in a timed step, so ignore nesting
			return statement.eval(ctxt);
		}
		else
		{
			long step = ctxt.threadState.CPU.getDuration(value);
			me.inOuterTimestep(true);
			Value rv = statement.eval(ctxt);
			me.inOuterTimestep(false);
			me.duration(step, ctxt, location);
			return rv;
		}
	}

	@Override
	public Statement findStatement(int lineno)
	{
		return statement.findStatement(lineno);
	}

	@Override
	public Expression findExpression(int lineno)
	{
		return statement.findExpression(lineno);
	}
}
