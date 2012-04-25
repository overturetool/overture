/*******************************************************************************
 *
 *	Copyright (c) 2008 Fujitsu Services Ltd.
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

package org.overturetool.vdmj.expressions;

import java.util.Iterator;

import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.ValueException;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.types.BooleanType;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.TypeList;
import org.overturetool.vdmj.util.Utils;
import org.overturetool.vdmj.values.BooleanValue;
import org.overturetool.vdmj.values.CompFunctionValue;
import org.overturetool.vdmj.values.FunctionValue;
import org.overturetool.vdmj.values.IterFunctionValue;
import org.overturetool.vdmj.values.Value;
import org.overturetool.vdmj.values.ValueList;


public class PreExpression extends Expression
{
	private static final long serialVersionUID = 1L;
	public final Expression function;
	public final ExpressionList args;

	public PreExpression(LexLocation location,
		Expression function, ExpressionList args)
	{
		super(location);
		this.function = function;
		this.args = args;
	}

	@Override
	public String toString()
	{
		return "pre_(" + function + Utils.listToString(args) + ")";
	}

	@Override
	public Type typeCheck(Environment env, TypeList qualifiers, NameScope scope)
	{
		function.typeCheck(env, null, scope);

		for (Expression a: args)
		{
			a.typeCheck(env, null, scope);
		}

		return new BooleanType(location);
	}

	@Override
	public Value eval(Context ctxt)
	{
		breakpoint.check(location, ctxt);

		Value fv = function.eval(ctxt);

		if (fv instanceof FunctionValue)
		{
			FunctionValue tfv = (FunctionValue)fv;

			while (true)
			{
    			if (tfv instanceof CompFunctionValue)
    			{
    				tfv = ((CompFunctionValue)tfv).ff1;
    				continue;
    			}

    			if (tfv instanceof IterFunctionValue)
    			{
    				tfv = ((IterFunctionValue)tfv).function;
    				continue;
    			}

    			break;
			}

			FunctionValue pref = tfv.precondition;

			if (pref == null)
			{
				return new BooleanValue(true);
			}

			if (pref.type.parameters.size() <= args.size())
			{
				try
				{
    				ValueList argvals = new ValueList();
    				Iterator<Expression> aiter = args.iterator();

    				for (@SuppressWarnings("unused") Type t: pref.type.parameters)
    				{
    					argvals.add(aiter.next().eval(ctxt));
    				}

					return pref.eval(location, argvals, ctxt);
				}
				catch (ValueException e)
				{
					abort(e);
				}
			}

			// else true, below.
		}

		return new BooleanValue(true);
	}

	@Override
	public String kind()
	{
		return "pre_";
	}
}
