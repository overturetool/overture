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
    		double lv = left.eval(ctxt).realValue(ctxt);
    		double rv = right.eval(ctxt).realValue(ctxt);

   			return NumericValue.valueOf(div(lv, rv), ctxt);
        }
        catch (ValueException e)
        {
        	return abort(e);
        }
	}

	static public long div(double lv, double rv)
	{
		/*
		 * There is often confusion on how integer division, remainder and modulus
		 * work on negative numbers. In fact, there are two valid answers to -14 div
		 * 3: either (the intuitive) -4 as in the Toolbox, or -5 as in e.g. Standard
		 * ML [Paulson91]. It is therefore appropriate to explain these operations in
		 * some detail.
		 *
		 * Integer division is defined using floor and real number division:
		 *
		 *		x/y < 0:	x div y = -floor(abs(-x/y))
		 *		x/y >= 0:	x div y = floor(abs(x/y))
		 *
		 * Note that the order of floor and abs on the right-hand side makes a difference,
		 * the above example would yield -5 if we changed the order. This is
		 * because floor always yields a smaller (or equal) integer, e.g. floor (14/3) is
		 * 4 while floor (-14/3) is -5.
		 */

		if (lv/rv < 0)
		{
			return (long)-Math.floor(Math.abs(lv/rv));
		}
		else
		{
			return (long)Math.floor(Math.abs(-lv/rv));
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
