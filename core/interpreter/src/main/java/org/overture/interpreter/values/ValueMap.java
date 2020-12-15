/*******************************************************************************
 *
 *	Copyright (c) 2016 Fujitsu Services Ltd.
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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.overture.interpreter.traces.PermuteArray;


/**
 * A map of value/values.
 * 
 * NOTE! As soon as an Invariant/Record value is added that defines "eq", we switch to
 * using an InvariantValueMap delegate, which only uses equals(). The problem is that
 * we only have "eq" and not a hashCode function defined, can which produce inconsistent
 * results with a HashMap (or TreeMap, with "ord" clauses).
 */
@SuppressWarnings("serial")
public class ValueMap extends HashMap<Value, Value>
{
	private InvariantValueMap delegate = null;
	
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
		if (delegate != null) return delegate.isInjective();

		Set<Value> rng = new HashSet<Value>(values());
		return keySet().size() == rng.size();
	}

	@Override
	public String toString()
	{
		if (delegate != null) return delegate.toString();

		StringBuilder sb = new StringBuilder();
		sb.append("{");
		String prefix = "";

		ValueList sortedKeys = new ValueList();
		sortedKeys.addAll(keySet());
		Collections.sort(sortedKeys);

		for (Value key: sortedKeys)		// For test comparisons
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
		if (delegate != null) return delegate.clone();

		ValueMap copy = new ValueMap();

		for (Value k: this.keySet())
		{
			Value kcopy = (Value)k.clone();
			Value vcopy = (Value)get(k).clone();
			copy.put(kcopy, vcopy);
		}

		return copy;
	}

	/**
	 * Returns a list of maps, with the map entries in all possible orders. This means
	 * that the Java Maps used have to be "Linked" Maps so that their order is preserved.
	 * This means that we cannot simply use ValueMaps, which are TreeMaps (sorted).
	 */
	public List<Map<Value, Value>> permutedMaps()
	{
		if (delegate != null) return delegate.permutedMaps();

		// This is a 1st order permutation, which does not take account of the possible
		// nesting of maps or the presence of other permutable values with them (sets).

		List<Map<Value, Value>> results = new Vector<Map<Value, Value>>();
		Object[] entries = entrySet().toArray();
		int size = entries.length;

		if (size == 0)
		{
			results.add(new LinkedHashMap<Value, Value>());	// Just {|->}
		}
		else
		{
			PermuteArray p = new PermuteArray(size);

			while (p.hasNext())
			{
				Map<Value, Value> m = new LinkedHashMap<Value, Value>();
				int[] perm = p.next();

				for (int i=0; i<size; i++)
				{
					@SuppressWarnings("unchecked")
					Entry<Value, Value> entry = (Entry<Value, Value>)entries[perm[i]];
					m.put(entry.getKey(), entry.getValue());
				}

				results.add(m);
			}
		}

		return results;
	}

	/**
	 * Remaining methods are simply delegated or passed up.
	 */

	@Override
	public int size()
	{
		if (delegate != null) return delegate.size(); else return super.size();
	}
	
	@Override
	public boolean equals(Object other)
	{
		if (delegate != null) return delegate.equals(other); else return super.equals(other);
	}

	@Override
	public boolean isEmpty()
	{
		if (delegate != null) return delegate.isEmpty(); else return super.isEmpty();
	}

	@Override
	public boolean containsKey(Object key)
	{
		if (delegate != null) return delegate.containsKey(key); else return super.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value)
	{
		if (delegate != null) return delegate.containsValue(value); else return super.containsValue(value);
	}

	@Override
	public Value get(Object key)
	{
		if (delegate != null) return delegate.get(key); else return super.get(key);
	}

	@Override
	public Value put(Value key, Value value)
	{
		if (delegate != null) return delegate.put(key, value);

		if (key instanceof InvariantValue)
		{
			InvariantValue ivalue = (InvariantValue)key;

			if (ivalue.getEq() != null)	// defines "eq"
			{
				delegate = new InvariantValueMap(this);
				return delegate.put(key, value);
			}
		}
		else if (key instanceof RecordValue)
		{
			RecordValue rvalue = (RecordValue)key;

			if (rvalue.equality != null)	// defines "eq"
			{
				delegate = new InvariantValueMap(this);
				return delegate.put(key, value);
			}
		}

		return super.put(key, value);
	}

	@Override
	public Value remove(Object key)
	{
		if (delegate != null) return delegate.remove(key); else return super.remove(key);
	}

	@Override
	public void putAll(Map<? extends Value, ? extends Value> m)
	{
		if (delegate != null) delegate.putAll(m); else super.putAll(m);
	}

	@Override
	public void clear()
	{
		if (delegate != null) delegate.clear(); else super.clear();
	}

	@Override
	public Set<Value> keySet()
	{
		if (delegate != null) return delegate.keySet(); else return super.keySet();
	}

	@Override
	public Collection<Value> values()
	{
		if (delegate != null) return delegate.values(); else return super.values();
	}

	@Override
	public Set<Map.Entry<Value, Value>> entrySet()
	{
		if (delegate != null) return delegate.entrySet(); else return super.entrySet();
	}
}
