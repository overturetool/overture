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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

import org.overture.interpreter.traces.PermuteArray;

/**
 * A map of value/values.
 */

@SuppressWarnings("serial")
public class ValueMap extends LinkedHashMap<Value, Value>
{
	public ValueMap()
	{
		super();
	}

	public ValueMap(ValueMap from)
	{
		putAll(from);
	}

	public ValueMap(Value k, Value v)
	{
		put(k, v);
	}

	public boolean isInjective()
	{
		Set<Value> rng = new HashSet<Value>(values());
		return keySet().size() == rng.size();
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		String prefix = "";

		for (Value key : this.keySet())
		{
			sb.append(prefix);
			sb.append(key);
			sb.append(" |-> ");
			sb.append(get(key));
			prefix = ", ";
		}

		sb.append("}");
		return sb.toString();
	}

	@Override
	public Object clone()
	{
		ValueMap copy = new ValueMap();

		for (Value k : this.keySet())
		{
			Value kcopy = (Value) k.clone();
			Value vcopy = (Value) get(k).clone();
			copy.put(kcopy, vcopy);
		}

		return copy;
	}

	public List<ValueMap> permutedMaps()
	{
		// This is a 1st order permutation, which does not take account of the possible
		// nesting of maps or the presence of other permutable values with them (sets).

		List<ValueMap> results = new ArrayList<ValueMap>();
		Object[] entries = entrySet().toArray();
		int size = entries.length;

		if (size == 0)
		{
			results.add(new ValueMap()); // Just {|->}
		} else
		{
			PermuteArray p = new PermuteArray(size);

			while (p.hasNext())
			{
				ValueMap m = new ValueMap();
				int[] perm = p.next();

				for (int i = 0; i < size; i++)
				{
					@SuppressWarnings("unchecked")
					Entry<Value, Value> entry = (Entry<Value, Value>) entries[perm[i]];
					m.put(entry.getKey(), entry.getValue());
				}

				results.add(m);
			}
		}

		return results;
	}
}
