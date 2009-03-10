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
import org.overturetool.vdmj.pog.FuncComposeObligation;
import org.overturetool.vdmj.pog.MapComposeObligation;
import org.overturetool.vdmj.pog.POContextStack;
import org.overturetool.vdmj.pog.ProofObligationList;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.ValueException;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.typechecker.TypeComparator;
import org.overturetool.vdmj.types.FunctionType;
import org.overturetool.vdmj.types.MapType;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.TypeList;
import org.overturetool.vdmj.types.TypeSet;
import org.overturetool.vdmj.types.UnknownType;
import org.overturetool.vdmj.values.CompFunctionValue;
import org.overturetool.vdmj.values.FunctionValue;
import org.overturetool.vdmj.values.MapValue;
import org.overturetool.vdmj.values.Value;
import org.overturetool.vdmj.values.ValueMap;

public class CompExpression extends BinaryExpression
{
	private static final long serialVersionUID = 1L;

	public CompExpression(Expression left, LexToken op, Expression right)
	{
		super(left, op, right);
	}

	@Override
	public Type typeCheck(Environment env, TypeList qualifiers, NameScope scope)
	{
		ltype = left.typeCheck(env, null, scope);
		rtype = right.typeCheck(env, null, scope);
		TypeSet results = new TypeSet();

		if (ltype.isMap())
		{
    		if (!rtype.isMap())
    		{
    			report(3068, "Right hand of map 'comp' is not a map");
    			detail("Type", rtype);
    			return new MapType(location);	// Unknown types
    		}

    		MapType lm = ltype.getMap();
    		MapType rm = rtype.getMap();

    		if (!TypeComparator.compatible(lm.from, rm.to))
    		{
    			report(3069, "Domain of left should equal range of right in map 'comp'");
    			detail2("Dom", lm.from, "Rng", rm.to);
    		}

    		results.add(new MapType(location, rm.from, lm.to));
		}

		if (ltype.isFunction())
		{
    		if (!rtype.isFunction())
    		{
    			report(3070, "Right hand of function 'comp' is not a function");
    			detail("Type", rtype);
    			return new UnknownType(location);
    		}
    		else
    		{
        		FunctionType lf = ltype.getFunction();
        		FunctionType rf = rtype.getFunction();

        		if (lf.parameters.size() != 1)
        		{
        			report(3071, "Left hand function must have a single parameter");
        			detail("Type", lf);
        		}
        		else if (rf.parameters.size() != 1)
        		{
        			report(3072, "Right hand function must have a single parameter");
        			detail("Type", rf);
        		}
        		else if (!TypeComparator.compatible(lf.parameters.get(0), rf.result))
        		{
        			report(3073, "Parameter of left should equal result of right in function 'comp'");
        			detail2("Parameter", lf.parameters.get(0), "Result", rf.result);
        		}

        		results.add(
        			new FunctionType(location, true, rf.parameters, lf.result));
    		}
		}

		if (results.isEmpty())
		{
			report(3074, "Left hand of 'comp' is neither a map nor a function");
			detail("Type", ltype);
			results.add(new UnknownType(location));
		}

		return results.getType(location);
	}

	@Override
	public Value eval(Context ctxt)
	{
		breakpoint.check(location, ctxt);

		Value lv = left.eval(ctxt).deref();
		Value rv = right.eval(ctxt).deref();

		if (lv instanceof MapValue)
		{
			ValueMap lm = null;
			ValueMap rm = null;

			try
			{
				lm = lv.mapValue(ctxt);
				rm = rv.mapValue(ctxt);
			}
			catch (ValueException e)
			{
				return abort(e);
			}

			ValueMap result = new ValueMap();

			for (Value v: rm.keySet())
			{
				Value rng = lm.get(rm.get(v));
				Value old = result.put(v, rng);

				if (old != null && !old.equals(rng))
				{
					abort(4005, "Duplicate map keys have different values", ctxt);
				}
			}

			return new MapValue(result);
		}

		try
		{
			FunctionValue f1 = lv.functionValue(ctxt);
			FunctionValue f2 = rv.functionValue(ctxt);

			return new CompFunctionValue(f1, f2);
		}
		catch (ValueException e)
		{
			return abort(e);
		}
	}

	@Override
	public ProofObligationList getProofObligations(POContextStack ctxt)
	{
		ProofObligationList obligations = new ProofObligationList();

		if (ltype.isFunction())
		{
			String pref1 = left.getPreName();
			String pref2 = right.getPreName();

			if (pref1 == null || !pref1.equals(""))
			{
				obligations.add(new FuncComposeObligation(
					this, pref1, pref2, ctxt));
			}
		}

		if (ltype.isMap())
		{
			obligations.add(new MapComposeObligation(this, ctxt));
		}

		return obligations;
	}

	@Override
	public String kind()
	{
		return "comp";
	}
}
