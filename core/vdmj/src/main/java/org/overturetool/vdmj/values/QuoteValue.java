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
import org.overturetool.vdmj.types.QuoteType;
import org.overturetool.vdmj.types.Type;

public class QuoteValue extends Value
{
	private static final long serialVersionUID = 1L;
	public final String value;

	public QuoteValue(String value)
	{
		this.value = value;
	}

	@Override
	public String quoteValue(Context ctxt)
	{
		return value;
	}

	@Override
	public boolean equals(Object other)
	{
		if (other instanceof Value)
		{
			Value val = ((Value)other).deref();

    		if (val instanceof QuoteValue)
    		{
    			QuoteValue ov = (QuoteValue)val;
    			return ov.value.equals(value);
    		}
		}

		return false;
	}

	@Override
	public String toString()
	{
		return "<" + value + ">";
	}

	@Override
	public int hashCode()
	{
		return value.hashCode();
	}

	@Override
	public String kind()
	{
		return toString();
	}

	@Override
	public Value convertValueTo(Type to, Context ctxt) throws ValueException
	{
		if (to instanceof QuoteType)
		{
			QuoteType qto = (QuoteType)to;

			if (!qto.value.equals(value))
			{
				abort(4074, "Cannot convert " + this + " to " + to, ctxt);
			}

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
		return new QuoteValue(value);
	}
}
