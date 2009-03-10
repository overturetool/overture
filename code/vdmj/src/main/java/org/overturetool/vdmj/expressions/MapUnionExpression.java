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

import org.overturetool.vdmj.lex.LexToken;
import org.overturetool.vdmj.pog.MapCompatibleObligation;
import org.overturetool.vdmj.pog.POContextStack;
import org.overturetool.vdmj.pog.ProofObligationList;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.ValueException;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.types.MapType;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.TypeList;
import org.overturetool.vdmj.types.TypeSet;
import org.overturetool.vdmj.values.MapValue;
import org.overturetool.vdmj.values.Value;
import org.overturetool.vdmj.values.ValueMap;


public class MapUnionExpression extends BinaryExpression
{
	private static final long serialVersionUID = 1L;

	public MapUnionExpression(Expression left, LexToken op, Expression right)
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
			report(3123, "Left hand of 'munion' is not a map");
			detail("Type", ltype);
			return new MapType(location);	// Unknown types
		}
		else if (!rtype.isMap())
		{
			report(3124, "Right hand of 'munion' is not a map");
			detail("Type", rtype);
			return ltype;
		}
		else
		{
			MapType ml = ltype.getMap();
			MapType mr = rtype.getMap();

			TypeSet from = new TypeSet(ml.from, mr.from);
			TypeSet to = new TypeSet(ml.to, mr.to);

			return new MapType(location,
				from.getType(location), to.getType(location));
		}
	}

	@Override
	public Value eval(Context ctxt)
	{
		breakpoint.check(location, ctxt);

		ValueMap lm = null;
		ValueMap rm = null;

		try
		{
			lm = left.eval(ctxt).mapValue(ctxt);
			rm = right.eval(ctxt).mapValue(ctxt);
		}
		catch (ValueException e)
		{
			return abort(e);
		}

		ValueMap result = new ValueMap();
		result.putAll(lm);
		Iterator<Value> i = rm.keySet().iterator();

		while (i.hasNext())
		{
			Value k = i.next();
			Value rng = rm.get(k);
			Value old = result.put(k, rng);

			if (old != null && !old.equals(rng))
			{
				abort(4021, "Duplicate map keys have different values: " + k, ctxt);
			}
		}

		return new MapValue(result);
	}

	@Override
	public ProofObligationList getProofObligations(POContextStack ctxt)
	{
		ProofObligationList obligations = super.getProofObligations(ctxt);
		obligations.add(new MapCompatibleObligation(left, right, ctxt));
		return obligations;
	}

	@Override
	public String kind()
	{
		return "munion";
	}
}
