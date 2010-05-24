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

import org.overturetool.vdmj.lex.LexRealToken;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.ValueException;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.types.IntegerType;
import org.overturetool.vdmj.types.NaturalOneType;
import org.overturetool.vdmj.types.NaturalType;
import org.overturetool.vdmj.types.RealType;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.TypeList;
import org.overturetool.vdmj.values.NumericValue;
import org.overturetool.vdmj.values.Value;

public class RealLiteralExpression extends Expression
{
	private static final long serialVersionUID = 1L;
	public final LexRealToken value;

	public RealLiteralExpression(LexRealToken value)
	{
		super(value.location);
		this.value = value;
	}

	@Override
	public String toString()
	{
		return value.toString();
	}

	@Override
	public Type typeCheck(Environment env, TypeList qualifiers, NameScope scope)
	{
		if (Math.round(value.value) == value.value)
		{
    		if (value.value < 0)
    		{
    			return new IntegerType(location);
    		}
    		else if (value.value == 0)
    		{
    			return new NaturalType(location);
    		}
    		else
    		{
    			return new NaturalOneType(location);
    		}
		}
		else
		{
			return new RealType(location);
		}
	}

	@Override
	public Value eval(Context ctxt)
	{
		breakpoint.check(location, ctxt);

		try
		{
			return NumericValue.valueOf(value.value, ctxt);
		}
		catch (ValueException e)
		{
			return abort(e);
		}
	}

	@Override
	public String kind()
	{
		return "literal";
	}
}
