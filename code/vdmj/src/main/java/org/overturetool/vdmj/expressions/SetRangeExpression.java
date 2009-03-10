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
import org.overturetool.vdmj.types.IntegerType;
import org.overturetool.vdmj.types.SetType;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.TypeList;
import org.overturetool.vdmj.values.IntegerValue;
import org.overturetool.vdmj.values.SetValue;
import org.overturetool.vdmj.values.Value;
import org.overturetool.vdmj.values.ValueSet;

public class SetRangeExpression extends SetExpression
{
	private static final long serialVersionUID = 1L;
	public final Expression first;
	public final Expression last;

	public Type ftype = null;
	public Type ltype = null;

	public SetRangeExpression(Expression first, Expression last)
	{
		super(first);
		this.first = first;
		this.last = last;
	}

	@Override
	public String toString()
	{
		return "{" + first + ", ... ," + last + "}";
	}

	@Override
	public Type typeCheck(Environment env, TypeList qualifiers, NameScope scope)
	{
		ftype = first.typeCheck(env, null, scope);
		ltype = last.typeCheck(env, null, scope);

		if (!ftype.isNumeric())
		{
			ftype.report(3166, "Set range type must be an number");
		}

		if (!ltype.isNumeric())
		{
			ltype.report(3167, "Set range type must be an number");
		}

		return new SetType(first.location, new IntegerType(location));
	}

	@Override
	public Value eval(Context ctxt)
	{
		breakpoint.check(location, ctxt);

		try
		{
    		long from = first.eval(ctxt).intValue(ctxt);
    		long to = last.eval(ctxt).intValue(ctxt);
    		ValueSet set = new ValueSet();

    		for (long i=from; i<= to; i++)
    		{
    			set.add(new IntegerValue(i));
    		}

    		return new SetValue(set);
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

		found = first.findExpression(lineno);
		if (found != null) return found;

		found = last.findExpression(lineno);
		if (found != null) return found;

		return null;
	}

	@Override
	public ProofObligationList getProofObligations(POContextStack ctxt)
	{
		ProofObligationList obligations = first.getProofObligations(ctxt);
		obligations.addAll(last.getProofObligations(ctxt));
		return obligations;
	}

	@Override
	public String kind()
	{
		return "set range";
	}
}
