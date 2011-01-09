/*******************************************************************************
 *
 *	Copyright (C) 2008, 2009 Fujitsu Services Ltd.
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

// This must be in the default package to work with VDMJ's native delegation.

import java.util.Random;

import org.overturetool.vdmj.runtime.ValueException;
import org.overturetool.vdmj.values.IntegerValue;
import org.overturetool.vdmj.values.NaturalOneValue;
import org.overturetool.vdmj.values.RealValue;
import org.overturetool.vdmj.values.Value;

public class MATH
{
	private static Random random = new Random();
	private static long seed = 0;

	public static Value sin(Value arg) throws ValueException, Exception
	{
		return new RealValue(Math.sin(arg.realValue(null)));
	}

	public static Value cos(Value arg) throws ValueException, Exception
	{
		return new RealValue(Math.cos(arg.realValue(null)));
	}

	public static Value tan(Value arg) throws ValueException, Exception
	{
		return new RealValue(Math.tan(arg.realValue(null)));
	}

	public static Value cot(Value arg) throws ValueException, Exception
	{
		return new RealValue(1/Math.tan(arg.realValue(null)));
	}

	public static Value asin(Value arg) throws ValueException, Exception
	{
		return new RealValue(Math.asin(arg.realValue(null)));
	}

	public static Value acos(Value arg) throws ValueException, Exception
	{
		return new RealValue(Math.acos(arg.realValue(null)));
	}

	public static Value atan(Value arg) throws ValueException, Exception
	{
		return new RealValue(Math.atan(arg.realValue(null)));
	}

	public static Value sqrt(Value arg) throws ValueException, Exception
	{
		return new RealValue(Math.sqrt(arg.realValue(null)));
	}

	public static Value pi(@SuppressWarnings("unused") Value arg)
		throws Exception
	{
		return new RealValue(Math.PI);
	}

	public static Value rand(Value arg) throws ValueException
	{
		long lv = arg.intValue(null);

		if (seed == -1)
		{
			return new IntegerValue(lv);
		}
		else if (lv == 0)
		{
			return new IntegerValue(0);
		}
		else
		{
			return new IntegerValue(Math.abs(random.nextLong() % lv));
		}
	}

	public static Value srand2(Value arg) throws ValueException
	{
		seed = arg.intValue(null);
		random.setSeed(seed);
		return new IntegerValue(seed);
	}

	public static Value exp(Value arg) throws ValueException, Exception
	{
		return new RealValue(Math.exp(arg.realValue(null)));
	}

	public static Value ln(Value arg) throws ValueException, Exception
	{
		return new RealValue(Math.log(arg.realValue(null)));
	}

	public static Value log(Value arg) throws ValueException, Exception
	{
		return new RealValue(Math.log10(arg.realValue(null)));
	}

	public static Value fac(Value arg) throws ValueException, Exception
	{
		return new NaturalOneValue(factorial(arg.natValue(null)));
	}

	private static long factorial(long n)
	{
		return (n < 1) ? 1 : n * factorial(n-1);
	}
}
