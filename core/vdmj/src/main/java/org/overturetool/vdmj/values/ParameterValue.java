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
import org.overturetool.vdmj.types.Type;

public class ParameterValue extends Value
{
	public final Type type;

	public ParameterValue(Type type)
	{
		this.type = type;
	}

	@Override
	public boolean equals(Object other)
	{
		// No need to dereference as we can't reference a parameter value

		if (other instanceof ParameterValue)
		{
			ParameterValue ov = (ParameterValue)other;
			return ov.type.equals(type);
		}

		return false;
	}

	@Override
	public String toString()
	{
		return type.toString();
	}

	@Override
	public int hashCode()
	{
		return type.hashCode();
	}

	@Override
	public String kind()
	{
		return "@type";
	}

	@Override
	public Value convertValueTo(Type to, Context ctxt) throws ValueException
	{
		abort(4073, "Cannot convert type parameter value to " + to, ctxt);
		return null;
	}

	@Override
	public Object clone()
	{
		return new ParameterValue(type);
	}
}
