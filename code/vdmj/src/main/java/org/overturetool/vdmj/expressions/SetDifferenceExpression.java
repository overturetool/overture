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

public class SetDifferenceExpression extends BinaryExpression
{
	public SetDifferenceExpression(Expression left, LexToken op, Expression right)
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
			report(3160, "Left hand of '\\' is not a set");
		}

		if (!rtype.isSet())
		{
			report(3161, "Right hand of '\\' is not a set");
		}

		if (!TypeComparator.compatible(ltype, rtype))
		{
			report(3162, "Left and right of '\\' are different types");
			detail2("Left", ltype, "Right", rtype);
		}

		return ltype;
	}

	@Override
	public Value eval(Context ctxt)
	{
		breakpoint.check(location, ctxt);

		ValueSet result = new ValueSet();
		ValueSet togo = null;

		try
		{
			togo = right.eval(ctxt).setValue(ctxt);
			result.addAll(left.eval(ctxt).setValue(ctxt));
		}
		catch (ValueException e)
		{
			return abort(e);
		}

		for (Value r: togo)
		{
			result.remove(r);
		}

		return new SetValue(result);
	}

	@Override
	public String kind()
	{
		return "set difference";
	}
}
