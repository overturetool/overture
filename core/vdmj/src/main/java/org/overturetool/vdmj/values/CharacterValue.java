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
import org.overturetool.vdmj.types.CharacterType;
import org.overturetool.vdmj.types.Type;

public class CharacterValue extends Value
{
	public final char unicode;

	public CharacterValue(char value)
	{
		this.unicode = value;
	}

	@Override
	public char charValue(Context ctxt)
	{
		return unicode;
	}

	@Override
	public boolean equals(Object other)
	{
		if (other instanceof Value)
		{
			Value val = ((Value)other).deref();

    		if (val instanceof CharacterValue)
    		{
    			CharacterValue ov = (CharacterValue)val;
    			return ov.unicode == unicode;
    		}
		}

		return false;
	}

	@Override
	public String toString()
	{
		if (Character.isISOControl(unicode))
		{
			return "'\\0" + Integer.toOctalString(unicode) + "'";
		}
		else
		{
			return "'" + unicode + "'";
		}
	}

	@Override
	public int hashCode()
	{
		return unicode;
	}

	@Override
	public String kind()
	{
		return "char";
	}

	@Override
	public Value convertValueTo(Type to, Context ctxt) throws ValueException
	{
		if (to.isType(CharacterType.class))
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
		return new CharacterValue(unicode);
	}
}
