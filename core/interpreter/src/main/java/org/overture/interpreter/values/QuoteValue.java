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

import java.util.Set;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.types.AQuoteType;
import org.overture.ast.types.PType;
import org.overture.interpreter.runtime.Context;

public class QuoteValue extends Value
{
	private static final long serialVersionUID = 1L;
	public final String value;

	public QuoteValue(String value)
	{
		this.value = value;
	}

	@Override
	public String quoteValue(Context ctxt)
	{
		return value;
	}

	@Override
	public boolean equals(Object other)
	{
		if (other instanceof Value)
		{
			Value val = ((Value) other).deref();

			if (val instanceof QuoteValue)
			{
				QuoteValue ov = (QuoteValue) val;
				return ov.value.equals(value);
			}
		}

		return false;
	}

	@Override
	public String toString()
	{
		return "<" + value + ">";
	}

	@Override
	public int hashCode()
	{
		return value.hashCode();
	}

	@Override
	public String kind()
	{
		return toString();
	}

	@Override
	protected Value convertValueTo(PType to, Context ctxt, Set<PType> done)
			throws AnalysisException
	{
		if (to instanceof AQuoteType)
		{
			AQuoteType qto = (AQuoteType) to;

			if (!qto.getValue().getValue().equals(value))
			{
				abort(4074, "Cannot convert " + this + " to " + to, ctxt);
			}

			return this;
		} else
		{
			return super.convertValueTo(to, ctxt, done);
		}
	}

	@Override
	public Object clone()
	{
		return new QuoteValue(value);
	}
}
