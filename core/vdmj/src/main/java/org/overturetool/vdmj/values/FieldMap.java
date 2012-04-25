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

import java.util.Iterator;
import java.util.Vector;

/**
 * A class to hold the name/Value list of fields in a record.
 */

@SuppressWarnings("serial")
public class FieldMap extends Vector<FieldValue>
{
	public FieldMap()
	{
		super();
	}

	public FieldMap(FieldMap from)
	{
		for (FieldValue fv: from)
		{
			add(fv);
		}
	}

	public FieldMap(String k, Value v, boolean comp)
	{
		add(k, v, comp);
	}

	@Override
	public boolean add(FieldValue fv)
	{
		return add(fv.name, fv.value, fv.comparable);
	}

	public boolean add(String k, Value v, boolean comp)
	{
		for (FieldValue fv: this)
		{
			if (fv.name.equals(k))
			{
				remove(fv);
				add(new FieldValue(k, v, comp));
				return false;
			}
		}

		return super.add(new FieldValue(k, v, comp));
	}

	public Value get(String key)
	{
		for (FieldValue fv: this)
		{
			if (fv.name.equals(key))
			{
				return fv.value;
			}
		}

		return null;
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("(");

		if (!isEmpty())
		{
			Iterator<FieldValue> i = iterator();
			FieldValue fv = i.next();
			sb.append(fv.toString());

			while (i.hasNext())
			{
				sb.append(", ");
				fv = i.next();
				sb.append(fv.toString());
			}
		}

		sb.append(")");
		return sb.toString();
	}

	@Override
	public int hashCode()
	{
		int hash = 0;

		for (FieldValue fv: this)
		{
			if (fv.comparable)
			{
				hash += fv.value.hashCode();
			}
		}

		return hash;
	}

	@Override
	public Object clone()
	{
		FieldMap copy = new FieldMap();

		for (FieldValue fv: this)
		{
			copy.add(fv.name, (Value)fv.value.clone(), fv.comparable);
		}

		return copy;
	}
}
