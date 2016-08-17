/*
 * #%~
 * VDM Code Generator Runtime
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
package org.overture.codegen.runtime;

import java.util.Random;

public class MATH
{

	public static final Number pi = 3.141592653589793;
	private static Random random = new Random();
	private static long seed = 0;

	public static Number sin(Number v)
	{
		return Math.sin(v.doubleValue());
	}

	public static Number cos(Number v)
	{
		return Math.cos(v.doubleValue());
	}

	public static Number tan(Number a)
	{
		return Math.tan(a.doubleValue());
	}

	public static Number cot(Number a)
	{
		return 1 / Math.tan(a.doubleValue());
	}

	public static Number asin(Number a)
	{
		return Math.asin(a.doubleValue());
	}

	public static Number acos(Number a)
	{
		return Math.acos(a.doubleValue());
	}

	public static Number atan(Number v)
	{
		return Math.atan(v.doubleValue());
	}

	public static Number acot(Number a)
	{
		return atan(1 / a.doubleValue());
	}

	public static Number sqrt(Number a)
	{
		return Math.sqrt(a.doubleValue());
	}

	public static Number pi_f()
	{
		return Math.PI;
	}

	public static void srand(Number a)
	{
		MATH.srand2(a);
	}

	public static Number rand(Number a)
	{
		long lv = a.longValue();

		if (seed == -1)
		{
			return lv;
		} else if (lv == 0)
		{
			return 0;
		} else
		{
			return Math.abs(random.nextLong() % lv);
		}
	}

	public static Number srand2(Number a)
	{
		seed = a.longValue();
		random.setSeed(seed);
		return seed;
	}

	public static Number exp(Number a)
	{

		return Math.exp(a.doubleValue());
	}

	public static Number ln(Number a)
	{

		return Math.log(a.doubleValue());
	}

	public static Number log(Number a)
	{

		return Math.log10(a.doubleValue());
	}

	public static Number fac(Number a)
	{
		return a.longValue() < 1 ? 1
				: a.longValue() * fac(a.longValue() - 1).longValue();
	}

}
