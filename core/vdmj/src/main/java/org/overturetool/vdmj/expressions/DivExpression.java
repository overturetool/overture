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
import org.overturetool.vdmj.pog.NonZeroObligation;
import org.overturetool.vdmj.pog.POContextStack;
import org.overturetool.vdmj.pog.ProofObligationList;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.ValueException;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.types.IntegerType;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.TypeList;
import org.overturetool.vdmj.values.NumericValue;
import org.overturetool.vdmj.values.Value;

public class DivExpression extends NumericBinaryExpression
{
	private static final long serialVersionUID = 1L;

	public DivExpression(Expression left, LexToken op, Expression right)
	{
		super(left, op, right);
	}

	@Override
	public Type typeCheck(Environment env, TypeList qualifiers, NameScope scope)
	{
		checkNumeric(env, scope);
		return new IntegerType(location);
	}

	@Override
	public Value eval(Context ctxt)
	{
		breakpoint.check(location, ctxt);

		try
		{
    		long lv = left.eval(ctxt).intValue(ctxt);
    		long rv = right.eval(ctxt).intValue(ctxt);

    		return NumericValue.valueOf(lv / rv, ctxt);
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

		if (!(right instanceof IntegerLiteralExpression) &&
			!(right instanceof RealLiteralExpression))
		{
			obligations.add(new NonZeroObligation(location, right, ctxt));
		}

		return obligations;
	}

	@Override
	public String kind()
	{
		return "div";
	}
}
