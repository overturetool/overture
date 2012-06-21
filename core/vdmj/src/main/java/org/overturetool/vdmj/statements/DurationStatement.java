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
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.ValueException;
import org.overturetool.vdmj.scheduler.BasicSchedulableThread;
import org.overturetool.vdmj.scheduler.ISchedulableThread;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.typechecker.TypeComparator;
import org.overturetool.vdmj.types.NaturalType;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.values.Value;

public class DurationStatement extends Statement
{
	private static final long serialVersionUID = 1L;
	public final Expression duration;
	public final Statement statement;

	public DurationStatement(
		LexLocation location, Expression duration, Statement stmt)
	{
		super(location);
		this.duration = duration;
		this.statement = stmt;
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
		Type argType = duration.typeCheck(env, null, scope);
		
		if (!TypeComparator.compatible(new NaturalType(location), argType))
		{
			duration.report(3281, "Arguments to duration must be a nat");
			detail("Actual", argType);
		}

		return statement.typeCheck(env, scope);
	}

	@Override
	public Value eval(Context ctxt)
	{
		location.hit();
		duration.location.hit();

		ISchedulableThread me = BasicSchedulableThread.getThread(Thread.currentThread());

		if (me.inOuterTimestep())
		{
			// Already in a timed step, so ignore nesting
			return statement.eval(ctxt);
		}
		else
		{
			try
			{
				// We disable the swapping and time (RT) as duration evaluation should be "free".
				ctxt.threadState.setAtomic(true);
				long step = duration.eval(ctxt).intValue(ctxt);
				ctxt.threadState.setAtomic(false);

				me.inOuterTimestep(true);
				Value rv = statement.eval(ctxt);
				me.inOuterTimestep(false);
				me.duration(step, ctxt, location);
				return rv;
			}
			catch (ValueException e)
			{
				abort(e);
				return null;
			}
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
