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

package org.overture.interpreter.values;

import java.util.FormattableFlags;
import java.util.Formatter;
import java.util.Set;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.types.ACharBasicType;
import org.overture.ast.types.PType;
import org.overture.interpreter.runtime.Context;

public class CharacterValue extends Value
{
	private static final long serialVersionUID = 1L;
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
			Value val = ((Value) other).deref();

			if (val instanceof CharacterValue)
			{
				CharacterValue ov = (CharacterValue) val;
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
		} else
		{
			return "'" + unicode + "'";
		}
	}

	@Override
	public void formatTo(Formatter formatter, int flags, int width,
			int precision)
	{
		String s;

		if ((flags & FormattableFlags.ALTERNATE) > 0)
		{
			s = toString();
		} else
		{
			s = "" + unicode;
		}

		formatTo(s, formatter, flags, width, precision);
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
	protected Value convertValueTo(PType to, Context ctxt, Set<PType> done)
			throws AnalysisException
	{
		if (to instanceof ACharBasicType)
		{
			return this;
		} else
		{
			return super.convertValueTo(to, ctxt, done);
		}
	}

	@Override
	public Object clone()
	{
		return new CharacterValue(unicode);
	}
}
