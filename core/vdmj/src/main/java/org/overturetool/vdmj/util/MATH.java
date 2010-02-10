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

package org.overturetool.vdmj.util;

import java.util.Random;

import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.ValueException;
import org.overturetool.vdmj.values.IntegerValue;
import org.overturetool.vdmj.values.RealValue;
import org.overturetool.vdmj.values.Value;

public class MATH
{
	private static Random random = new Random();
	private static long seed = 0;

	public static Value sin(Context ctxt) throws ValueException, Exception
	{
		Value arg = ctxt.lookup(new LexNameToken("MATH", "v", null));
		return new RealValue(Math.sin(arg.realValue(ctxt)));
	}

	public static Value cos(Context ctxt) throws ValueException, Exception
	{
		Value arg = ctxt.lookup(new LexNameToken("MATH", "v", null));
		return new RealValue(Math.cos(arg.realValue(ctxt)));
	}

	public static Value tan(Context ctxt) throws ValueException, Exception
	{
		Value arg = ctxt.lookup(new LexNameToken("MATH", "a", null));
		return new RealValue(Math.tan(arg.realValue(ctxt)));
	}

	public static Value cot(Context ctxt) throws ValueException, Exception
	{
		Value arg = ctxt.lookup(new LexNameToken("MATH", "a", null));
		return new RealValue(1/Math.tan(arg.realValue(ctxt)));
	}

	public static Value asin(Context ctxt) throws ValueException, Exception
	{
		Value arg = ctxt.lookup(new LexNameToken("MATH", "a", null));
		return new RealValue(Math.asin(arg.realValue(ctxt)));
	}

	public static Value acos(Context ctxt) throws ValueException, Exception
	{
		Value arg = ctxt.lookup(new LexNameToken("MATH", "a", null));
		return new RealValue(Math.acos(arg.realValue(ctxt)));
	}

	public static Value atan(Context ctxt) throws ValueException, Exception
	{
		Value arg = ctxt.lookup(new LexNameToken("MATH", "v", null));
		return new RealValue(Math.atan(arg.realValue(ctxt)));
	}

	public static Value sqrt(Context ctxt) throws ValueException, Exception
	{
		Value arg = ctxt.lookup(new LexNameToken("MATH", "a", null));
		return new RealValue(Math.sqrt(arg.realValue(ctxt)));
	}

	public static Value pi(@SuppressWarnings("unused") Context ctxt)
		throws Exception
	{
		return new RealValue(Math.PI);
	}

	public static Value rand(Context ctxt) throws ValueException
	{
		Value arg = ctxt.lookup(new LexNameToken("MATH", "a", null));
		long lv = arg.intValue(ctxt);

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

	public static Value srand2(Context ctxt) throws ValueException
	{
		Value arg = ctxt.lookup(new LexNameToken("MATH", "a", null));
		seed = arg.intValue(ctxt);
		random.setSeed(seed);
		return new IntegerValue(seed);
	}

	public static Value exp(Context ctxt) throws ValueException, Exception
	{
		Value arg = ctxt.lookup(new LexNameToken("MATH", "a", null));
		return new RealValue(Math.exp(arg.realValue(ctxt)));
	}

	public static Value ln(Context ctxt) throws ValueException, Exception
	{
		Value arg = ctxt.lookup(new LexNameToken("MATH", "a", null));
		return new RealValue(Math.log(arg.realValue(ctxt)));
	}

	public static Value log(Context ctxt) throws ValueException, Exception
	{
		Value arg = ctxt.lookup(new LexNameToken("MATH", "a", null));
		return new RealValue(Math.log10(arg.realValue(ctxt)));
	}
}
