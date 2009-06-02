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

import org.overturetool.vdmj.messages.MessageException;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.ValueException;
import org.overturetool.vdmj.types.NaturalType;
import org.overturetool.vdmj.types.Type;

public class NaturalValue extends IntegerValue
{
	private static final long serialVersionUID = 1L;

	public NaturalValue(long value) throws Exception
	{
		super(value);

		if (value < 0)
		{
			throw new Exception("Value " + value + " is not a nat");
		}
	}

	@Override
	public String kind()
	{
		return "nat";
	}

	@Override
	public Value convertValueTo(Type to, Context ctxt) throws ValueException
	{
		if (to instanceof NaturalType)
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
			return new NaturalValue(longVal);
		}
		catch (Exception e)
		{
			throw new MessageException("Internal 0005: Illegal clone");
		}
	}
}
