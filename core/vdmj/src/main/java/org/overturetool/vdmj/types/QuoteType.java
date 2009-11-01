/*******************************************************************************
 *
 *	Copyright (c) 2008 Fujitsu Services Ltd.
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

package org.overturetool.vdmj.types;

import org.overturetool.vdmj.lex.LexQuoteToken;
import org.overturetool.vdmj.values.QuoteValue;
import org.overturetool.vdmj.values.ValueList;

public class QuoteType extends Type
{
	private static final long serialVersionUID = 1L;
	public final String value;

	public QuoteType(LexQuoteToken token)
	{
		super(token.location);
		value = token.value;
	}

	@Override
	public String toDisplay()
	{
		return "<" + value + ">";
	}

	@Override
	public boolean equals(Object other)
	{
		while (other instanceof BracketType)
		{
			other = ((BracketType)other).type;
		}

		if (other instanceof QuoteType)
		{
			QuoteType qother = (QuoteType)other;
			return this.value.equals(qother.value);
		}

		return false;
	}

	@Override
	public int hashCode()
	{
		return value.hashCode();
	}

	@Override
	public ValueList getAllValues()
	{
		ValueList v = new ValueList();
		v.add(new QuoteValue(value));
		return v;
	}
}
