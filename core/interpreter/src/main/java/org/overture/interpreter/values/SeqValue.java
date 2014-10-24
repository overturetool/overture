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
import org.overture.ast.lex.LexStringToken;
import org.overture.ast.types.ASeq1SeqType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SSeqType;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.ValueException;

public class SeqValue extends Value
{
	private static final long serialVersionUID = 1L;
	public final ValueList values;

	public SeqValue()
	{
		values = new ValueList();
	}

	public SeqValue(ValueList values)
	{
		this.values = values;
	}

	public SeqValue(String s)
	{
		this.values = new ValueList();
		int len = s.length();

		for (int i = 0; i < len; i++)
		{
			this.values.add(new CharacterValue(s.charAt(i)));
		}
	}

	public SeqValue(LexStringToken string)
	{
		this(string.value);
	}

	@Override
	public ValueList seqValue(Context ctxt)
	{
		return values;
	}

	@Override
	public String stringValue(Context ctxt)
	{
		String s = values.toString();

		if (s.charAt(0) == '"')
		{
			return s.substring(1, s.length() - 1);
		}

		return s;
	}

	@Override
	public void formatTo(Formatter formatter, int flags, int width,
			int precision)
	{
		String s = values.toString();

		if ((flags & FormattableFlags.ALTERNATE) == 0 && s.charAt(0) == '"')
		{
			s = s.substring(1, s.length() - 1);
		}

		formatTo(s, formatter, flags, width, precision);
	}

	@Override
	public Value getUpdatable(ValueListenerList listeners)
	{
		ValueList nseq = new ValueList();

		for (Value k : values)
		{
			Value v = k.getUpdatable(listeners);
			nseq.add(v);
		}

		return UpdatableValue.factory(new SeqValue(nseq), listeners);
	}

	@Override
	public Value getConstant()
	{
		return new SeqValue(values.getConstant());
	}

	public Value get(Value arg, Context ctxt) throws ValueException
	{
		int i = (int) arg.nat1Value(ctxt);

		if (i < 1 || i > values.size())
		{
			abort(4083, "Sequence index out of range: " + arg, ctxt);
		}

		return values.get(i - 1); // NB 1st = 1. Throws IndexOutOfBounds
	}

	@Override
	public boolean equals(Object other)
	{
		if (other instanceof Value)
		{
			Value val = ((Value) other).deref();

			if (val instanceof SeqValue)
			{
				SeqValue ot = (SeqValue) val;
				return values.equals(ot.values);
			}
		}

		return false;
	}

	@Override
	public String toString()
	{
		return values.toString();
	}

	@Override
	public int hashCode()
	{
		return values.hashCode();
	}

	@Override
	public String kind()
	{
		return "seq";
	}

	@Override
	protected Value convertValueTo(PType to, Context ctxt, Set<PType> done)
			throws AnalysisException
	{
		// We can't use the isSeq method as it plucks out one sequence
		// value from a union. We need to try all union members. So we
		// only test for pure SeqTypes.

		if (to instanceof SSeqType)
		{
			if (to instanceof ASeq1SeqType && values.isEmpty())
			{
				abort(4084, "Cannot convert empty sequence to seq1", ctxt);
			}

			SSeqType seqto = (SSeqType) to;
			ValueList nl = new ValueList();

			for (Value v : values)
			{
				nl.add(v.convertValueTo(seqto.getSeqof(), ctxt));
			}

			return new SeqValue(nl);
		} else
		{
			return super.convertValueTo(to, ctxt, done);
		}
	}

	@Override
	public Object clone()
	{
		return new SeqValue((ValueList) values.clone());
	}
}
