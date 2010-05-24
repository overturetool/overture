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
import org.overturetool.vdmj.types.NaturalOneType;
import org.overturetool.vdmj.types.NaturalType;
import org.overturetool.vdmj.types.NumericType;
import org.overturetool.vdmj.types.RealType;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.TypeList;
import org.overturetool.vdmj.values.NumericValue;
import org.overturetool.vdmj.values.Value;

public class PlusExpression extends NumericBinaryExpression
{
	private static final long serialVersionUID = 1L;

	public PlusExpression(Expression left, LexToken op, Expression right)
	{
		super(left, op, right);
	}

	@Override
	public Type typeCheck(Environment env, TypeList qualifiers, NameScope scope)
	{
		checkNumeric(env, scope);

		NumericType ln = ltype.getNumeric();
		NumericType rn = rtype.getNumeric();

		if (ln instanceof RealType)
		{
			return ln;
		}
		else if (rn instanceof RealType)
		{
			return rn;
		}
		else if (ln instanceof IntegerType)
		{
			return ln;
		}
		else if (rn instanceof IntegerType)
		{
			return rn;
		}
		else if (ln instanceof NaturalType && rn instanceof NaturalType)
		{
			return ln;
		}
		else
		{
			return new NaturalOneType(ln.location);
		}
	}

	@Override
	public Value eval(Context ctxt)
	{
		breakpoint.check(location, ctxt);

		try
		{
    		double lv = left.eval(ctxt).realValue(ctxt);
    		double rv = right.eval(ctxt).realValue(ctxt);

    		return NumericValue.valueOf(lv + rv, ctxt);
    	}
    	catch (ValueException e)
    	{
    		return abort(e);
    	}
	}

	@Override
	public String kind()
	{
		return "plus";
	}
}
