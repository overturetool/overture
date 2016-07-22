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

import java.io.Serializable;

import org.overture.interpreter.runtime.ValueException;
import org.overture.interpreter.values.Value;

/**
 * This class delegates native operations to the IO/Math/VDMUtil classes for flat specifications.
 */

public class DEFAULT implements Serializable
{
	private static final long serialVersionUID = 1L;

	//
	// IO...
	//

	public static Value writeval(Value tval)
	{
		return IO.writeval(tval);
	}

	public static Value fwriteval(Value fval, Value tval, Value dval)
	{
		return IO.fwriteval(fval, tval, dval);
	}

	// public static Value freadval(Value fval)
	// {
	// return IO.freadval(fval);
	// }

	public static Value fecho(Value fval, Value tval, Value dval)
	{
		return IO.fecho(fval, tval, dval);
	}

	public static Value ferror()
	{
		return IO.ferror();
	}

	public static Value print(Value v)
	{
		return IO.print(v);
	}

	public static Value printf(Value fv, Value vs) throws ValueException
	{
		return IO.printf(fv, vs);
	}

	//
	// MATH...
	//

	public static Value sin(Value arg) throws ValueException, Exception
	{
		return MATH.sin(arg);
	}

	public static Value cos(Value arg) throws ValueException, Exception
	{
		return MATH.cos(arg);
	}

	public static Value tan(Value arg) throws ValueException, Exception
	{
		return MATH.tan(arg);
	}

	public static Value cot(Value arg) throws ValueException, Exception
	{
		return MATH.cot(arg);
	}

	public static Value asin(Value arg) throws ValueException, Exception
	{
		return MATH.asin(arg);
	}

	public static Value acos(Value arg) throws ValueException, Exception
	{
		return MATH.acos(arg);
	}

	public static Value atan(Value arg) throws ValueException, Exception
	{
		return MATH.atan(arg);
	}

	public static Value sqrt(Value arg) throws ValueException, Exception
	{
		return MATH.sqrt(arg);
	}

	public static Value pi_f() throws Exception
	{
		return MATH.pi_f();
	}

	public static Value rand(Value arg) throws ValueException
	{
		return MATH.rand(arg);
	}

	public static Value srand2(Value arg) throws ValueException
	{
		return MATH.srand2(arg);
	}

	public static Value exp(Value arg) throws ValueException, Exception
	{
		return MATH.exp(arg);
	}

	public static Value ln(Value arg) throws ValueException, Exception
	{
		return MATH.ln(arg);
	}

	public static Value log(Value arg) throws ValueException, Exception
	{
		return MATH.log(arg);
	}

	public static Value fac(Value arg) throws ValueException, Exception
	{
		return MATH.fac(arg);
	}

	//
	// VDMUtil...
	//

	public static Value set2seq(Value arg) throws ValueException
	{
		return VDMUtil.set2seq(arg);
	}

	public static Value val2seq_of_char(Value arg)
	{
		return VDMUtil.val2seq_of_char(arg);
	}

	public static Value seq_of_char2val_(Value arg)
	{
		return VDMUtil.seq_of_char2val_(arg);
	}
}
