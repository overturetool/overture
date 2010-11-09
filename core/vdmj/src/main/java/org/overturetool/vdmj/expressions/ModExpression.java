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
import org.overturetool.vdmj.types.IntegerType;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.TypeList;
import org.overturetool.vdmj.values.NumericValue;
import org.overturetool.vdmj.values.Value;

public class ModExpression extends NumericBinaryExpression
{
	private static final long serialVersionUID = 1L;

	public ModExpression(Expression left, LexToken op, Expression right)
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
			/*
			 * Remainder x rem y and modulus x mod y are the same if the signs of x
			 * and y are the same, otherwise they differ and rem takes the sign of x and
			 * mod takes the sign of y. The formulas for remainder and modulus are:
			 *
			 *		x rem y = x - y * (x div y)
			 *		x mod y = x - y * floor(x/y)
			 *
			 * Hence, -14 rem 3 equals -2 and -14 mod 3 equals 1. One can view these
			 * results by walking the real axis, starting at -14 and making jumps of 3.
			 * The remainder will be the last negative number one visits, because the first
			 * argument corresponding to x is negative, while the modulus will be the first
			 * positive number one visit, because the second argument corresponding to y
			 * is positive.
			 */

    		double lv = left.eval(ctxt).intValue(ctxt);
    		double rv = right.eval(ctxt).intValue(ctxt);

    		return NumericValue.valueOf(lv - rv * (long)Math.floor(lv/rv), ctxt);
		}
		catch (ValueException e)
		{
			return abort(e);
		}
	}

	@Override
	public String kind()
	{
		return "mod";
	}
}
