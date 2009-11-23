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

import java.util.List;
import java.util.Vector;

import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.pog.MapSeqOfCompatibleObligation;
import org.overturetool.vdmj.pog.POContextStack;
import org.overturetool.vdmj.pog.ProofObligationList;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.types.MapType;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.TypeList;
import org.overturetool.vdmj.types.TypeSet;
import org.overturetool.vdmj.util.Utils;
import org.overturetool.vdmj.values.MapValue;
import org.overturetool.vdmj.values.Value;
import org.overturetool.vdmj.values.ValueMap;


public class MapEnumExpression extends MapExpression
{
	private static final long serialVersionUID = 1L;
	public final List<MapletExpression> members;
	public TypeList domtypes = null;
	public TypeList rngtypes = null;

	public MapEnumExpression(LexLocation location)
	{
		super(location);
		members = new Vector<MapletExpression>();
	}

	public MapEnumExpression(LexLocation location, List<MapletExpression> members)
	{
		super(location);
		this.members = members;
	}

	@Override
	public String toString()
	{
		return "{" + Utils.listToString(members) + "}";
	}

	@Override
	public Type typeCheck(Environment env, TypeList qualifiers, NameScope scope)
	{
		domtypes = new TypeList();
		rngtypes = new TypeList();

		if (members.isEmpty())
		{
			return new MapType(location);
		}

		TypeSet dom = new TypeSet();
		TypeSet rng = new TypeSet();

		for (Expression ex: members)
		{
			Type mt = ex.typeCheck(env, null, scope);

			if (!mt.isMap())
			{
				report(3121, "Element is not of maplet type");
			}
			else
			{
				MapType maplet = mt.getMap();
				dom.add(maplet.from);
				domtypes.add(maplet.from);
				rng.add(maplet.to);
				rngtypes.add(maplet.to);
			}
		}

		return new MapType(location, dom.getType(location), rng.getType(location));
	}

	@Override
	public Value eval(Context ctxt)
	{
		breakpoint.check(location, ctxt);

		ValueMap map = new ValueMap();

		for (MapletExpression e: members)
		{
			Value l = e.left.eval(ctxt);
			Value r = e.right.eval(ctxt);
			e.location.hit();
			Value old = map.put(l, r);

			if (old != null && !old.equals(r))
			{
				abort(4017, "Duplicate map keys have different values: " + l, ctxt);
			}
		}

		return new MapValue(map);
	}

	@Override
	public Expression findExpression(int lineno)
	{
		Expression found = super.findExpression(lineno);
		if (found != null) return found;

		for (MapletExpression m: members)
		{
			found = m.findExpression(lineno);
			if (found != null) return found;
		}

		return null;
	}

	@Override
	public ProofObligationList getProofObligations(POContextStack ctxt)
	{
		ProofObligationList list = new ProofObligationList();

		for (MapletExpression maplet: members)
		{
			list.addAll(maplet.getProofObligations(ctxt));
		}

		if (members.size() > 1)
		{
			list.add(new MapSeqOfCompatibleObligation(this, ctxt));
		}

		return list;
	}

	@Override
	public String kind()
	{
		return "map enumeration";
	}
}
