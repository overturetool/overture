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

import org.overturetool.vdmj.lex.LexToken;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.ValueException;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.typechecker.TypeComparator;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.TypeList;
import org.overturetool.vdmj.values.SetValue;
import org.overturetool.vdmj.values.Value;
import org.overturetool.vdmj.values.ValueSet;

public class SetIntersectExpression extends BinaryExpression
{
	private static final long serialVersionUID = 1L;

	public SetIntersectExpression(Expression left, LexToken op, Expression right)
	{
		super(left, op, right);
	}

	@Override
	public Type typeCheck(Environment env, TypeList qualifiers, NameScope scope)
	{
		ltype = left.typeCheck(env, null, scope);
		rtype = right.typeCheck(env, null, scope);

		if (!ltype.isSet())
		{
			report(3163, "Left hand of " + location + " is not a set");
		}

		if (!rtype.isSet())
		{
			report(3164, "Right hand of " + location + " is not a set");
		}

		if (!TypeComparator.compatible(ltype, rtype))
		{
			report(3165, "Left and right of intersect are different types");
			detail2("Left", ltype, "Right", rtype);
		}

		return ltype;
	}

	@Override
	public Value eval(Context ctxt)
	{
		breakpoint.check(location, ctxt);

		try
		{
    		ValueSet result = new ValueSet();
    		result.addAll(left.eval(ctxt).setValue(ctxt));
    		result.retainAll(right.eval(ctxt).setValue(ctxt));
    		return new SetValue(result);
		}
		catch (ValueException e)
		{
			return abort(e);
		}
	}

	@Override
	public String kind()
	{
		return "intersect";
	}
}
