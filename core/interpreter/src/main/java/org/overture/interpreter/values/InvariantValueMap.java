/*******************************************************************************
 *
 *	Copyright (c) 2020 Fujitsu Services Ltd.
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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.overture.interpreter.traces.PermuteArray;

/**
 * A map of value/values.
 * 
 * NOTE! In order to allow maps of values with "eq/ord" clauses, we cannot use
 * standard Java HashMaps or TreeMaps, as these may break if the user wants
 * eq/ord clauses that do not meet the Java contracts for equals() and compareTo().
 */
public class InvariantValueMap implements Map<Value, Value>
{
	private static class Entry implements Map.Entry<Value, Value>
	{
		private Value key;
		private Value value;
		
		public Entry(Value key, Value value)
		{
			this.key = key;
			this.value = value;
		}
		
		@Override
		public Value getKey()
		{
			return key;
		}

		@Override
		public Value getValue()
		{
			return value;
		}

		@Override
		public Value setValue(Value value)
		{
			Value old = value;
			this.value = value;
			return old;
		}
	}
	
	// These will always be the same size!
	private List<Value> domain = new Vector<Value>();
	private List<Value> range = new Vector<Value>();
	private int size = 0;
	
	public InvariantValueMap()
	{
		super();
	}

	public InvariantValueMap(ValueMap from)
	{
		putAll(from);
	}

	public boolean isInjective()
	{
		for (int i=0; i<size; i++)
		{
			Value v = range.get(i);
			
			for (int j=i+1; j<size; j++)
			{
				if (range.get(j).equals(v))
				{
					return false;
				}
			}
		}

		return true;
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		String prefix = "";

		for (int i=0; i<size; i++)
		{
			sb.append(prefix);
			sb.append(domain.get(i));
			sb.append(" |-> ");
			sb.append(range.get(i));
			prefix = ", ";
		}

		sb.append("}");
		return sb.toString();
	}

	@Override
	public Object clone()
	{
		InvariantValueMap copy = new InvariantValueMap();

		for (int i=0; i<size; i++)
		{
			Value kcopy = (Value)domain.get(i).clone();
			Value vcopy = (Value)range.get(i).clone();
			copy.put(kcopy, vcopy);
		}

		return copy;
	}

	/**
	 * Returns a list of maps, with the map entries in all possible orders.
	 */
	public List<Map<Value, Value>> permutedMaps()
	{
		// This is a 1st order permutation, which does not take account of the possible
		// nesting of maps or the presence of other permutable values with them (sets).

		List<Map<Value, Value>> results = new Vector<Map<Value, Value>>();

		if (size == 0)
		{
			results.add(new ValueMap());	// Just {|->}
		}
		else
		{
			PermuteArray p = new PermuteArray(size);

			while (p.hasNext())
			{
				InvariantValueMap m = new InvariantValueMap();
				int[] perm = p.next();

				for (int i=0; i<perm.length; i++)
				{
					m.put(domain.get(perm[i]), range.get(perm[i]));
				}

				results.add(m);
			}
		}

		return results;
	}

	@Override
	public int size()
	{
		return size;
	}
	
	@Override
	public boolean equals(Object other)
	{
		if (other == this)
		{
			return true;
		}
		else if (other instanceof InvariantValueMap)
		{
			InvariantValueMap vmap = (InvariantValueMap)other;
			
			if (size != vmap.size)
			{
				return false;
			}
			
			for (int i=0; i<size; i++)
			{
				if (!range.get(i).equals(vmap.get(domain.get(i))))
				{
					return false;
				}
			}
			
			return true;
		}
		else if (other instanceof ValueMap)
		{
			ValueMap vmap = (ValueMap)other;
			
			if (size != vmap.size())
			{
				return false;
			}
			
			for (int i=0; i<size; i++)
			{
				if (!range.get(i).equals(vmap.get(domain.get(i))))
				{
					return false;
				}
			}
			
			return true;
		}
		else
		{
			return false;
		}
	}

	@Override
	public boolean isEmpty()
	{
		return domain.isEmpty();
	}

	@Override
	public boolean containsKey(Object key)
	{
		return domain.contains(key);
	}

	@Override
	public boolean containsValue(Object value)
	{
		return range.contains(value);
	}

	@Override
	public Value get(Object key)
	{
		for (int i=0; i<size; i++)
		{
			if (domain.get(i).equals(key))	// NB. uses "eq" 
			{
				return range.get(i);
			}
		}
		
		return null;
	}

	@Override
	public Value put(Value key, Value value)
	{
		Value old = null;
		
		for (int i=0; i<size; i++)
		{
			if (domain.get(i).equals(key))	// NB. uses "eq" 
			{
				old = range.get(i);
				range.set(i, value);
				return old;
			}
		}

		domain.add(key);
		range.add(value);
		size++;
		
		return null;
	}

	@Override
	public Value remove(Object key)
	{
		Value old = null;
		
		for (int i=0; i<size; i++)
		{
			if (domain.get(i).equals(key))
			{
				old = range.get(i);
				domain.remove(i);
				range.remove(i);
				size--;
			}
		}
		
		return old;
	}

	@Override
	public void putAll(Map<? extends Value, ? extends Value> m)
	{
		for (Value key: m.keySet())
		{
			put(key, m.get(key));
		}
	}

	@Override
	public void clear()
	{
		domain.clear();
		range.clear();
		size = 0;
	}

	@Override
	public Set<Value> keySet()
	{
		return new LinkedHashSet<Value>(domain);
	}

	@Override
	public Collection<Value> values()
	{
		return range;
	}

	@Override
	public Set<Map.Entry<Value, Value>> entrySet()
	{
		// NOTE this uses a LinkedHashSet to give a predictable iterator order
		Set<Map.Entry<Value, Value>> set = new LinkedHashSet<Map.Entry<Value, Value>>();
		
		for (int i=0; i<size; i++)
		{
			set.add(new Entry(domain.get(i), range.get(i)));
		}
		
		return set;
	}
}
