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

	public static final double pi = 3.141592653589793;
	private static Random random = new Random();
	private static long seed = 0;
	
	public static double sin(Number v)
	{
		return Math.sin(v.doubleValue());
	}

	public static double cos(Number v)
	{
		return Math.cos(v.doubleValue());
	}

	public static double tan(Number a)
	{
		return Math.tan(a.doubleValue());
	}

	public static double cot(Number a)
	{
		return 1/Math.tan(a.doubleValue());
	}

	public static double asin(Number a)
	{
		return Math.asin(a.doubleValue());
	}

	public static double acos(Number a)
	{
		return Math.acos(a.doubleValue());
	}

	public static double atan(Number v)
	{
		return Math.atan(v.doubleValue());
	}

	public static double acot(Number a)
	{
		return atan(1 / a.doubleValue());
	}

	public static double sqrt(Number a)
	{
		return Math.sqrt(a.doubleValue());
	}

	public static double pi_f()
	{
		return Math.PI;
	}

	public static void srand(Number a)
	{
		MATH.srand2(a);
	}

	public static long rand(Number a)
	{
		long lv = a.longValue();

		if (seed == -1)
		{
			return lv;
		}
		else if (lv == 0)
		{
			return 0;
		}
		else
		{
			return Math.abs(random.nextLong() % lv);
		}
	}

	public static long srand2(Number a)
	{
		seed = a.longValue();
		random.setSeed(seed);
		return seed;
	}

	public static double exp(Number a)
	{

		return Math.exp(a.doubleValue());
	}

	public static double ln(Number a)
	{

		return Math.log(a.doubleValue());
	}

	public static double log(Number a)
	{

		return Math.log10(a.doubleValue());
	}

	public static long fac(Number a)
	{
		return (a.longValue() < 1) ? 1 : a.longValue() * fac(a.longValue()-1);
	}

}
