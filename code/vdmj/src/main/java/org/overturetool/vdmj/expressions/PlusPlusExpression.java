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
import org.overturetool.vdmj.pog.POContextStack;
import org.overturetool.vdmj.pog.ProofObligationList;
import org.overturetool.vdmj.pog.SeqModificationObligation;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.ValueException;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.types.MapType;
import org.overturetool.vdmj.types.NumericType;
import org.overturetool.vdmj.types.SeqType;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.TypeList;
import org.overturetool.vdmj.types.TypeSet;
import org.overturetool.vdmj.types.UnknownType;
import org.overturetool.vdmj.values.MapValue;
import org.overturetool.vdmj.values.SeqValue;
import org.overturetool.vdmj.values.Value;
import org.overturetool.vdmj.values.ValueList;
import org.overturetool.vdmj.values.ValueMap;

public class PlusPlusExpression extends BinaryExpression
{
	private static final long serialVersionUID = 1L;

	public PlusPlusExpression(Expression left, LexToken op, Expression right)
	{
		super(left, op, right);
	}

	@Override
	public Type typeCheck(Environment env, TypeList qualifiers, NameScope scope)
	{
		ltype = left.typeCheck(env, null, scope);
		rtype = right.typeCheck(env, null, scope);

		TypeSet result = new TypeSet();
		boolean unique = (!ltype.isUnion() && !rtype.isUnion());

		if (ltype.isMap())
		{
    		if (!rtype.isMap())
    		{
    			concern(unique, 3141, "Right hand of '++' is not a map");
    			detail(unique, "Type", rtype);
    			return new MapType(location);	// Unknown types
    		}

    		MapType lm = ltype.getMap();
    		MapType rm = rtype.getMap();

    		TypeSet domain = new TypeSet(lm.from, rm.from);
    		TypeSet range = new TypeSet(lm.to, rm.to);

    		result.add(new MapType(location,
    			domain.getType(location), range.getType(location)));
		}

		if (ltype.isSeq())
		{
    		SeqType st = ltype.getSeq();

    		if (!rtype.isMap())
    		{
    			concern(unique, 3142, "Right hand of '++' is not a map");
    			detail(unique, "Type", rtype);
    		}
    		else
    		{
        		MapType mr = rtype.getMap();

        		if (!mr.from.isType(NumericType.class))
        		{
        			concern(unique, 3143, "Domain of right hand of '++' must be nat1");
        			detail(unique, "Type", mr.from);
        		}
    		}

    		result.add(st);
		}

		if (result.isEmpty())
		{
			report(3144, "Left of '++' is neither a map nor a sequence");
			return new UnknownType(location);
		}

		return result.getType(location);
	}

	@Override
	public Value eval(Context ctxt)
	{
		breakpoint.check(location, ctxt);

		try
		{
    		Value lv = left.eval(ctxt).deref();
    		Value rv = right.eval(ctxt);

    		if (lv instanceof MapValue)
    		{
    			ValueMap lm = new ValueMap(lv.mapValue(ctxt));
    			ValueMap rm = rv.mapValue(ctxt);

    			for (Value k: rm.keySet())
    			{
					lm.put(k, rm.get(k));
				}

    			return new MapValue(lm);
    		}
    		else
    		{
    			ValueList seq = lv.seqValue(ctxt);
    			ValueMap map = rv.mapValue(ctxt);
    			ValueList result = new ValueList(seq);

    			for (Value k: map.keySet())
    			{
					int iv = (int)k.intValue(ctxt);

					if (iv < 1 || iv > seq.size())
					{
						abort(4025, "Map key not within sequence index range: " + k, ctxt);
					}

					result.set(iv-1, map.get(k));
    			}

    			return new SeqValue(result);
    		}
		}
		catch (ValueException e)
		{
			return abort(e);
		}
	}

	@Override
	public ProofObligationList getProofObligations(POContextStack ctxt)
	{
		ProofObligationList obligations = super.getProofObligations(ctxt);

		if (ltype.isSeq())
		{
			obligations.add(new SeqModificationObligation(this, ctxt));
		}

		return obligations;
	}

	@Override
	public String kind()
	{
		return "++";
	}
}
