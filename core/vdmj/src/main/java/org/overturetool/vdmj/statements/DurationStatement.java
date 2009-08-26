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
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.values.Value;

public class DurationStatement extends Statement
{
	private static final long serialVersionUID = 1L;
	public final Expression duration;
	public final Statement statement;

	private long step = 0;

	public DurationStatement(
		LexLocation location, Expression duration, Statement stmt)
	{
		super(location);
		this.duration = duration;
		this.statement = stmt;
	}

	@Override
	public Value eval(Context ctxt)
	{
		if (ctxt.threadState.getTimestep() > 0)
		{
			// Already in a timed step, so ignore nesting
			return statement.eval(ctxt);
		}
		else
		{
			ctxt.threadState.setTimestep(step);
			Value rv = statement.eval(ctxt);
			ctxt.threadState.CPU.duration(step);
			ctxt.threadState.setTimestep(0);
			return rv;
		}
	}

	@Override
	public String kind()
	{
		return "duration";
	}

	@Override
	public String toString()
	{
		return "duration (" + duration + ") " + statement;
	}

	@Override
	public Type typeCheck(Environment env, NameScope scope)
	{
		if (duration instanceof IntegerLiteralExpression)
		{
			IntegerLiteralExpression i = (IntegerLiteralExpression)duration;

			if (i.value.value < 0)
			{
				duration.report(3281, "Arguments to duration must be integer >= 0");
			}

			step = i.value.value;
		}
		else if (duration instanceof RealLiteralExpression)
		{
			RealLiteralExpression i = (RealLiteralExpression)duration;

			if (i.value.value < 0 ||
				Math.floor(i.value.value) != i.value.value)
			{
				duration.report(3282, "Argument to duration must be integer >= 0");
			}
		}
		else
		{
			duration.report(3281, "Arguments to duration must be integer >= 0");
		}

		return statement.typeCheck(env, scope);
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
