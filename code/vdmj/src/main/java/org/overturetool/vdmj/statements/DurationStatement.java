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
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.values.Value;

public class DurationStatement extends Statement
{
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
	public Value eval(Context ctxt)
	{
		return statement.eval(ctxt);
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
		if (!(duration instanceof IntegerLiteralExpression))
		{
			duration.report(3281, "Arguments to duration must be >= 0");
		}
		else
		{
			IntegerLiteralExpression i = (IntegerLiteralExpression)duration;

			if (i.value.value < 0)
			{
				duration.report(3281, "Arguments to duration must be >= 0");
			}
		}

		return statement.typeCheck(env, scope);
	}
}
