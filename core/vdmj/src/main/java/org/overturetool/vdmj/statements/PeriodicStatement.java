/*******************************************************************************
 *
 *	Copyright (C) 2008 Fujitsu Services Ltd.
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

import org.overturetool.vdmj.Settings;
import org.overturetool.vdmj.definitions.Definition;
import org.overturetool.vdmj.definitions.ExplicitOperationDefinition;
import org.overturetool.vdmj.definitions.ImplicitOperationDefinition;
import org.overturetool.vdmj.expressions.Expression;
import org.overturetool.vdmj.expressions.ExpressionList;
import org.overturetool.vdmj.expressions.IntegerLiteralExpression;
import org.overturetool.vdmj.expressions.RealLiteralExpression;
import org.overturetool.vdmj.lex.Dialect;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.runtime.Breakpoint;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.ValueException;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.types.OperationType;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.TypeList;
import org.overturetool.vdmj.types.UnknownType;
import org.overturetool.vdmj.types.VoidType;
import org.overturetool.vdmj.util.Utils;
import org.overturetool.vdmj.values.OperationValue;
import org.overturetool.vdmj.values.Value;
import org.overturetool.vdmj.values.ValueList;

public class PeriodicStatement extends Statement
{
	private static final long serialVersionUID = 1L;
	public final LexNameToken opname;
	public final ExpressionList args;

	public long[] values = new long[4];

	public PeriodicStatement(LexNameToken opname, ExpressionList args)
	{
		super(opname.location);
		this.opname = opname;
		this.args = args;
	}

	@Override
	public Type typeCheck(Environment env, NameScope scope)
	{
		int nargs = (Settings.dialect == Dialect.VDM_RT) ? 4 : 1;

		if (args.size() != nargs)
		{
			report(3287, "Periodic thread must have " + nargs + " argument(s)");
		}
		else
		{
			int i = 0;

			for (Expression arg: args)
			{
				values[i] = -1;

				if (arg instanceof IntegerLiteralExpression)
				{
					IntegerLiteralExpression e = (IntegerLiteralExpression)arg;
					values[i] = e.value.value;
				}
				else if (arg instanceof RealLiteralExpression)
				{
					RealLiteralExpression r = (RealLiteralExpression)arg;
					values[i] = Math.round(r.value.value);
				}

				if (values[i] < 0)
				{
					arg.report(2027, "Expecting +ive literal number in periodic statement");
				}

				i++;
			}

			if (values[0] == 0)
			{
				args.get(0).report(3288, "Period argument must be non-zero");
			}

			if (args.size() == 4)
			{
				if (values[2] >= values[0])
				{
					args.get(2).report(
						3289, "Delay argument must be less than the period");
				}
			}
		}

		opname.setTypeQualifier(new TypeList());
		Definition opdef = env.findName(opname, NameScope.NAMES);

		if (opdef == null)
		{
			report(3228, opname + " is not in scope");
			return new UnknownType(location);
		}

		// Operation must be "() ==> ()"

		OperationType expected =
			new OperationType(location, new TypeList(), new VoidType(location));

		if (opdef instanceof ExplicitOperationDefinition)
		{
			ExplicitOperationDefinition def = (ExplicitOperationDefinition)opdef;

			if (!def.type.equals(expected))
			{
				report(3229, opname + " should have no parameters or return type");
				detail("Actual", def.type);
			}
		}
		else if (opdef instanceof ImplicitOperationDefinition)
		{
			ImplicitOperationDefinition def = (ImplicitOperationDefinition)opdef;

			if (def.body == null)
			{
				report(3230, opname + " is implicit");
			}

			if (!def.type.equals(expected))
			{
				report(3231, opname + " should have no parameters or return type");
				detail("Actual", def.type);
			}
		}
		else
		{
			report(3232, opname + " is not an operation name");
		}

		return new VoidType(location);
	}

	@Override
	public Value eval(Context ctxt)
	{
		location.hit();

		try
		{
			OperationValue op = ctxt.lookup(opname).operationValue(ctxt);
			long period = values[0];

			while (true)
			{
				long start = System.currentTimeMillis();
				op.eval(location, new ValueList(), ctxt);
				long duration = System.currentTimeMillis() - start;

				try
				{
					Thread.sleep(duration > period ? 1 : period - duration);
				}
				catch (InterruptedException e)
				{
					Breakpoint.handleInterrupt(location, ctxt);
				}
			}
		}
		catch (ValueException e)
		{
			return abort(e);
		}
	}

	@Override
	public String kind()
	{
		return "periodic statement";
	}

	@Override
	public String toString()
	{
		return "periodic(" + Utils.listToString(args) + ")(" + opname + ")";
	}
}
