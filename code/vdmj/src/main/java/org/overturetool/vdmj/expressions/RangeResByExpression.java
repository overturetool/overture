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
import org.overturetool.vdmj.types.MapType;
import org.overturetool.vdmj.types.SetType;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.TypeList;
import org.overturetool.vdmj.values.MapValue;
import org.overturetool.vdmj.values.Value;
import org.overturetool.vdmj.values.ValueMap;
import org.overturetool.vdmj.values.ValueSet;

public class RangeResByExpression extends BinaryExpression
{
	public RangeResByExpression(Expression left, LexToken op, Expression right)
	{
		super(left, op, right);
	}

	@Override
	public Type typeCheck(Environment env, TypeList qualifiers, NameScope scope)
	{
		ltype = left.typeCheck(env, null, scope);
		rtype = right.typeCheck(env, null, scope);

		if (!ltype.isMap())
		{
			report(3148, "Left of ':->' is not a map");
		}
		else if (!rtype.isSet())
		{
			report(3149, "Right of ':->' is not a set");
		}
		else
		{
			MapType map = ltype.getMap();
			SetType set = rtype.getSet();

			if (!TypeComparator.compatible(set.setof, map.to))
			{
				report(3150, "Restriction of map should be set of " + map.to);
			}
		}

		return ltype;
	}

	@Override
	public Value eval(Context ctxt)
	{
		breakpoint.check(location, ctxt);

		ValueSet set = null;
		ValueMap map = null;

		try
		{
			set = right.eval(ctxt).setValue(ctxt);
			map = left.eval(ctxt).mapValue(ctxt);
		}
		catch (ValueException e)
		{
			return abort(e);
		}

		ValueMap modified = new ValueMap(map);

		for (Value k: map.keySet())
		{
			if (set.contains(map.get(k)))
			{
				modified.remove(k);
			}
		}

		return new MapValue(modified);
	}

	@Override
	public String kind()
	{
		return ":->";
	}
}
