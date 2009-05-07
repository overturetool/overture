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

public class UndefinedValue extends Value
{
	@Override
	public String toString()
	{
		return "undefined";
	}

	@Override
	public boolean equals(Object other)
	{
		return false;
	}

	@Override
	public int hashCode()
	{
		return 0;
	}

	@Override
	public String kind()
	{
		return toString();
	}

	@Override
	public Value convertValueTo(Type to, Context ctxt) throws ValueException
	{
		return abort(4132, "Using undefined value", ctxt);
	}

	@Override
	public boolean isUndefined()
	{
		return true;
	}

	@Override
	public Object clone()
	{
		return new UndefinedValue();
	}
}
