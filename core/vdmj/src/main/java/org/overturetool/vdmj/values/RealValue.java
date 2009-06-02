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
import org.overturetool.vdmj.types.RealType;
import org.overturetool.vdmj.types.Type;

public class RealValue extends NumericValue
{
	private static final long serialVersionUID = 1L;

	public RealValue(double value) throws Exception
	{
		super(value);

		if (Double.isInfinite(value))
		{
			throw new Exception("Real is infinite");
		}
		else if (Double.isNaN(value))
		{
			throw new Exception("Real is NaN");
		}
	}

	public RealValue(long value)
	{
		super(value);
	}

	@Override
	public int compareTo(Value other)
	{
		if (other instanceof RealValue)
		{
			RealValue ro = (RealValue)other;
			return (int)Math.round(Math.signum(value - ro.value));
		}

		return super.compareTo(other);
	}

	@Override
	public double realValue(Context ctxt)
	{
		return value;
	}

	@Override
	public double ratValue(Context ctxt)
	{
		return value;
	}

	@Override
	public long intValue(Context ctxt) throws ValueException
	{
		long rounded = Math.round(value);

		if (rounded != value)
		{
			abort(4075, "Value " + value + " is not an integer", ctxt);
		}

		return rounded;
	}

	@Override
	public long nat1Value(Context ctxt) throws ValueException
	{
		long rounded = Math.round(value);

		if (rounded != value || rounded < 1)
		{
			abort(4076, "Value " + value + " is not a nat1", ctxt);
		}

		return rounded;
	}

	@Override
	public long natValue(Context ctxt) throws ValueException
	{
		long rounded = Math.round(value);

		if (rounded != value || rounded < 0)
		{
			abort(4077, "Value " + value + " is not a nat", ctxt);
		}

		return rounded;
	}

	@Override
	public String toString()
	{
		return Double.toString(value);
	}

	@Override
	public int hashCode()
	{
		return new Double(value).hashCode();
	}

	@Override
	public String kind()
	{
		return "real";
	}

	@Override
	public Value convertValueTo(Type to, Context ctxt) throws ValueException
	{
		if (to instanceof RealType)
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
		try
		{
			return new RealValue(value);
		}
		catch (Exception e)
		{
			// Can't happen?
			return null;
		}
	}
}
