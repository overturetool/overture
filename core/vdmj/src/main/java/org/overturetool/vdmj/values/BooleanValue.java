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

import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.ValueException;
import org.overturetool.vdmj.types.BooleanType;
import org.overturetool.vdmj.types.Type;

public class BooleanValue extends Value
{
	private static final long serialVersionUID = 1L;
	public final boolean value;

	public BooleanValue(boolean value)
	{
		this.value = value;
	}

	@Override
	public boolean boolValue(Context ctxt)
	{
		return value;
	}

	@Override
	public boolean equals(Object other)
	{
		if (other instanceof Value)
		{
			Value val = ((Value)other).deref();

    		if (val instanceof BooleanValue)
    		{
    			BooleanValue ov = (BooleanValue)val;
    			return ov.value == value;
    		}
		}

		return false;
	}

	@Override
	public String toString()
	{
		return Boolean.toString(value);
	}

	@Override
	public int hashCode()
	{
		return value ? 1 : 0;
	}

	@Override
	public String kind()
	{
		 return "bool";
	}

	@Override
	public Value convertValueTo(Type to, Context ctxt) throws ValueException
	{
		if (to.isType(BooleanType.class))
		{
			return this;
		}
		else
		{
			return super.convertValueTo(to, ctxt);
		}
	}

	@Override
	public Object clone()
	{
		return new BooleanValue(value);
	}
}
