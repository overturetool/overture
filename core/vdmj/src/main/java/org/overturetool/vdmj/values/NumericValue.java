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
import org.overturetool.vdmj.types.IntegerType;
import org.overturetool.vdmj.types.NaturalOneType;
import org.overturetool.vdmj.types.NaturalType;
import org.overturetool.vdmj.types.RationalType;
import org.overturetool.vdmj.types.RealType;
import org.overturetool.vdmj.types.Type;

public abstract class NumericValue extends Value
{
	public double value;

	public NumericValue(double value)
	{
		super();
		this.value = value;
	}

	public static NumericValue valueOf(double d, Context ctxt) throws ValueException
	{
		if (Double.isInfinite(d) || Double.isNaN(d))
		{
			throw new ValueException(4134, "Infinite or NaN trouble", ctxt);
		}

		long rounded = Math.round(d);

		if (rounded != d)
		{
			try
			{
				return new RealValue(d);
			}
			catch (Exception e)
			{
				throw new ValueException(4134, e.getMessage(), ctxt);
			}
		}

		return valueOf(rounded, ctxt);
	}

	public static NumericValue valueOf(long iv, Context ctxt) throws ValueException
	{
		if (iv > 0)
		{
			try
			{
				return new NaturalOneValue(iv);
			}
			catch (Exception e)
			{
				throw new ValueException(4064, e.getMessage(), ctxt);
			}
		}

		if (iv >= 0)
		{
			try
			{
				return new NaturalValue(iv);
			}
			catch (Exception e)
			{
				throw new ValueException(4065, e.getMessage(), ctxt);
			}
		}

		return new IntegerValue(iv);
	}

	@Override
	public Value convertValueTo(Type to, Context ctxt) throws ValueException
	{
		if (to instanceof RealType)
		{
			try
			{
				return new RealValue(realValue(ctxt));
			}
			catch (Exception e)
			{
				throw new ValueException(4134, e.getMessage(), ctxt);
			}
		}
		else if (to instanceof RationalType)
		{
			try
			{
				return new RationalValue(ratValue(ctxt));
			}
			catch (Exception e)
			{
				throw new ValueException(4134, e.getMessage(), ctxt);
			}
		}
		else if (to instanceof IntegerType)
		{
			return new IntegerValue(intValue(ctxt));
		}
		else if (to instanceof NaturalType)
		{
			try
			{
				return new NaturalValue(natValue(ctxt));
			}
			catch (Exception e)
			{
				return abort(4065, e.getMessage(), ctxt);
			}
		}
		else if (to instanceof NaturalOneType)
		{
			try
			{
				return new NaturalOneValue(nat1Value(ctxt));
			}
			catch (Exception e)
			{
				return abort(4064, e.getMessage(), ctxt);
			}
		}
		else
		{
			return super.convertValueTo(to, ctxt);
		}
	}

	@Override
	public boolean equals(Object other)
	{
		if (other instanceof Value)
		{
			Value val = ((Value)other).deref();

			if (val instanceof NumericValue)
			{
				NumericValue nov = (NumericValue)val;
				return nov.value == value;
			}
		}

		return false;
	}

	@Override
	abstract public double realValue(Context ctxt) throws ValueException;
	@Override
	abstract public double ratValue(Context ctxt) throws ValueException;
	@Override
	abstract public long intValue(Context ctxt) throws ValueException;
	@Override
	abstract public long natValue(Context ctxt) throws ValueException;
	@Override
	abstract public long nat1Value(Context ctxt) throws ValueException;
	@Override
	abstract public int hashCode();
	@Override
	abstract public String toString();
}
