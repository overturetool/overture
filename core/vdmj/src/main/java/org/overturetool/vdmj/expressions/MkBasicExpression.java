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

import org.overturetool.vdmj.pog.POContextStack;
import org.overturetool.vdmj.pog.ProofObligationList;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.ValueException;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.types.TokenType;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.TypeList;
import org.overturetool.vdmj.values.TokenValue;
import org.overturetool.vdmj.values.Value;
import org.overturetool.vdmj.values.ValueList;

public class MkBasicExpression extends Expression
{
	private static final long serialVersionUID = 1L;
	public final Type type;
	public final Expression arg;

	public MkBasicExpression(Type type, Expression arg)
	{
		super(type.location);
		this.type = type;
		this.arg = arg;
	}

	@Override
	public String toString()
	{
		return "mk_" + type + "(" + arg + ")";
	}

	@Override
	public Type typeCheck(Environment env, TypeList qualifiers, NameScope scope)
	{
		Type argtype = arg.typeCheck(env, null, scope);

		if (!(type instanceof TokenType) && !argtype.equals(type))
		{
			report(3125, "Argument of mk_" + type + " is the wrong type");
		}

		return type;
	}

	@Override
	public Value eval(Context ctxt)
	{
		breakpoint.check(location, ctxt);

		Value v = arg.eval(ctxt);

		if (type instanceof TokenType)
		{
			return new TokenValue(v);
		}
		else
		{
			try
			{
				v = v.convertTo(type, ctxt);
			}
			catch (ValueException e)
			{
				abort(4022, "mk_ type argument is not " + type, ctxt);
			}
		}

		return v;
	}

	@Override
	public Expression findExpression(int lineno)
	{
		Expression found = super.findExpression(lineno);
		if (found != null) return found;

		return arg.findExpression(lineno);
	}

	@Override
	public ProofObligationList getProofObligations(POContextStack ctxt)
	{
		return arg.getProofObligations(ctxt);
	}

	@Override
	public String kind()
	{
		return "mk_";
	}

	@Override
	public ValueList getValues(Context ctxt)
	{
		return arg.getValues(ctxt);
	}
}
