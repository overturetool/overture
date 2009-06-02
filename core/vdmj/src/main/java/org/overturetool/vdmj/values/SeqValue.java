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

import org.overturetool.vdmj.lex.LexStringToken;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.ValueException;
import org.overturetool.vdmj.types.Seq1Type;
import org.overturetool.vdmj.types.SeqType;
import org.overturetool.vdmj.types.Type;

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

		for (int i=0; i<len; i++)
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
	public Value getUpdatable(ValueListener listener)
	{
		ValueList nseq = new ValueList();

		for (Value k: values)
		{
			Value v = k.getUpdatable(listener);
			nseq.add(v);
		}

		return new UpdatableValue(new SeqValue(nseq), listener);
	}

	public Value get(Value arg, Context ctxt) throws ValueException
	{
		int i = (int)arg.nat1Value(ctxt);

		if (i < 1 || i > values.size())
		{
			abort(4083, "Sequence index out of range: " + arg, ctxt);
		}

		return values.get(i-1);		// NB 1st = 1. Throws IndexOutOfBounds
	}

	@Override
	public boolean equals(Object other)
	{
		if (other instanceof Value)
		{
			Value val = ((Value)other).deref();

    		if (val instanceof SeqValue)
    		{
    			SeqValue ot = (SeqValue)val;
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
	public Value convertValueTo(Type to, Context ctxt) throws ValueException
	{
		// We can't use the isSeq method as it plucks out one sequence
		// value from a union. We need to try all union members. So we
		// only test for pure SeqTypes.

		if (to instanceof SeqType)
		{
			if (to instanceof Seq1Type && values.isEmpty())
			{
				abort(4084, "Cannot convert empty sequence to seq1", ctxt);
			}

			SeqType seqto = (SeqType)to;
			ValueList nl = new ValueList();

			for (Value v: values)
			{
				nl.add(v.convertValueTo(seqto.seqof, ctxt));
			}

			return new SeqValue(nl);
		}
		else
		{
			return super.convertValueTo(to, ctxt);
		}
	}

	@Override
	public Object clone()
	{
		return new SeqValue((ValueList)values.clone());
	}
}
