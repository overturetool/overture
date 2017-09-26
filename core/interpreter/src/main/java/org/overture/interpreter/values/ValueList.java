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

import java.util.Vector;

import org.overture.ast.util.Utils;

/**
 * A sequential list of values.
 */

@SuppressWarnings("serial")
public class ValueList extends Vector<Value>
{
	public ValueList()
	{
		super();
	}

	public ValueList(Value[] from)
	{
		for (Value value: from)
		{
			add(value);
		}
	}

	public ValueList(ValueList from)
	{
		addAll(from);
	}

	public ValueList(Value v)
	{
		add(v);
	}

	public ValueList(int n)
	{
		super(n);
	}

	public boolean inbounds(int i)
	{
		return i >= 0 && i < size();
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();

		if (isEmpty())
		{
			sb.append("[]");
		} else
		{
			sb.append("\"");

			for (Value v : this)
			{
				v = v.deref();

				if (!(v instanceof CharacterValue))
				{
					return Utils.listToString("[", this, ", ", "]");
				}

				CharacterValue ch = (CharacterValue) v;

				switch (ch.unicode)
				{
				// case '\n':
				// sb.append("\\n");
				// break;

				// case '\t':
				// sb.append("\\t");
				// break;

				// case '\'':
				// sb.append("\\\'");
				// break;

					case '\r':
						sb.append("\\r");
						break;

					case '\f':
						sb.append("\\f");
						break;

					case '\033':
						sb.append("\\e");
						break;

					case '\007':
						sb.append("\\a");
						break;

					case '\"':
						sb.append("\\\"");
						break;

					case '\\':
						sb.append("\\\\");
						break;

					default:
						sb.append(ch.unicode);
				}
			}

			sb.append("\"");
		}

		return sb.toString();
	}

	@Override
	public Object clone()
	{
		ValueList copy = new ValueList();

		for (Value v : this)
		{
			Value vcopy = (Value) v.clone();
			copy.add(vcopy);
		}

		return copy;
	}

	public ValueList getConstant()
	{
		ValueList nseq = new ValueList();

		for (Value k : this)
		{
			Value v = k.getConstant();
			nseq.add(v);
		}

		return nseq;
	}
}
