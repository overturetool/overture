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

package org.overturetool.vdmj.expressions;

import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.pog.POContextStack;
import org.overturetool.vdmj.pog.ProofObligationList;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.ValueException;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.types.BooleanType;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.TypeList;
import org.overturetool.vdmj.values.BooleanValue;
import org.overturetool.vdmj.values.ObjectValue;
import org.overturetool.vdmj.values.Value;

public class SameClassExpression extends Expression
{
	private static final long serialVersionUID = 1L;
	public final Expression left;
	public final Expression right;

	public SameClassExpression(LexLocation start, ExpressionList args)
	{
		super(start);

		left = args.get(0);
		right = args.get(1);
	}

	@Override
	public Value eval(Context ctxt)
	{
		try
		{
			Value l = left.eval(ctxt);
			Value r = right.eval(ctxt);

			if (!l.isType(ObjectValue.class) ||
				!r.isType(ObjectValue.class))
			{
				return new BooleanValue(false);
			}

			ObjectValue lv = l.objectValue(ctxt);
			ObjectValue rv = r.objectValue(ctxt);

			return new BooleanValue(lv.type.equals(rv.type));
		}
		catch (ValueException e)
		{
			return abort(e);
		}
	}

	@Override
	public Expression findExpression(int lineno)
	{
		Expression found = super.findExpression(lineno);
		if (found != null) return found;

		found = left.findExpression(lineno);
		if (found != null) return found;

		found = right.findExpression(lineno);
		if (found != null) return found;

		return null;
	}

	@Override
	public ProofObligationList getProofObligations(POContextStack ctxt)
	{
		ProofObligationList list = left.getProofObligations(ctxt);
		list.addAll(right.getProofObligations(ctxt));
		return list;
	}

	@Override
	public String kind()
	{
		return "sameclass";
	}

	@Override
	public String toString()
	{
		return "sameclass(" + left + "," + right + ")";
	}

	@Override
	public Type typeCheck(Environment env, TypeList qualifiers, NameScope scope)
	{
		Type lt = left.typeCheck(env, null, scope);

		if (!lt.isClass())
		{
			left.report(3266, "Argument is not an object");
		}

		Type rt = right.typeCheck(env, null, scope);

		if (!rt.isClass())
		{
			right.report(3266, "Argument is not an object");
		}

		return new BooleanType(location);
	}
}
