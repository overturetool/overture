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


public class IterFunctionValue extends FunctionValue
{
	private static final long serialVersionUID = 1L;
	public final FunctionValue function;
	public final long iterations;

	public IterFunctionValue(FunctionValue function, long iterations)
	{
		super(function.location, function.type, "**");
		this.function = function;
		this.iterations = iterations;
	}

	@Override
	public String toString()
	{
		return function.toString() + " ** " + iterations;
	}

	@Override
	public Value eval(
		LexLocation from, ValueList argValues, Context ctxt) throws ValueException
	{
		Value result = function.eval(from, argValues, ctxt);

		for (int i=1; i<iterations; i++)
		{
			result = function.eval(from, result, ctxt);
		}

		return result;
	}

	@Override
	public boolean equals(Object other)
	{
		if (other instanceof Value)
		{
			Value val = ((Value)other).deref();

			if (val instanceof IterFunctionValue)
    		{
    			IterFunctionValue ov = (IterFunctionValue)val;
    			return ov.function.equals(function) && ov.iterations == iterations;
    		}
		}

		return false;
	}

	@Override
	public int hashCode()
	{
		return function.hashCode() + (int)iterations;
	}

	@Override
	public String kind()
	{
		return "**";
	}
}
