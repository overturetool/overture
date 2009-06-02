/*******************************************************************************
 *
 *	Copyright (C) 2008 Fujitsu Services Ltd.
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

package org.overturetool.vdmj.values;

import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.ValueException;



public class CompFunctionValue extends FunctionValue
{
	private static final long serialVersionUID = 1L;
	public final FunctionValue ff1;
	public final FunctionValue ff2;

	public CompFunctionValue(FunctionValue f1, FunctionValue f2)
	{
		super(f1.location, "comp");
		this.ff1 = f1;
		this.ff2 = f2;
	}

	@Override
	public String toString()
	{
		return ff2.type.parameters + " -> " + ff1.type.result;
	}

	@Override
	public Value eval(
		LexLocation from, ValueList argValues, Context ctxt) throws ValueException
	{
		ValueList f1arg = new ValueList();
		f1arg.add(ff2.eval(from, argValues, ctxt));
		return ff1.eval(from, f1arg, ctxt);
	}

	@Override
	public boolean equals(Object other)
	{
		if (other instanceof Value)
		{
			Value val = ((Value)other).deref();

    		if (val instanceof CompFunctionValue)
    		{
    			CompFunctionValue ov = (CompFunctionValue)val;
    			return ov.ff1.equals(ff1) && ov.ff2.equals(ff2);
    		}
		}

		return false;
	}

	@Override
	public int hashCode()
	{
		return ff1.hashCode() + ff2.hashCode();
	}

	@Override
	public String kind()
	{
		return "comp";
	}
}
