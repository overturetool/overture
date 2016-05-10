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

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import org.overture.ast.util.Utils;
import org.overture.interpreter.traces.PermuteArray;

/**
 * A set of values. Note that although this class implements a set (no duplicates) it is not based on a
 * {@code java.util.Set<Value>}, but rather a {@code Vector<Value>}. This is so that the possible orderings of set values can be
 * enumerated when performing quantifiers like {@code "a,b,c in set {{1,2,3}, {4,5,6}}"}.
 */

public class ValueSet extends Vector<Value> // NB based on Vector
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1364718882230227399L;
	private boolean isSorted;

	public ValueSet()
	{
		super();
		isSorted = true;
	}

	public ValueSet(int size)
	{
		super(size);
		isSorted = true;
	}

	public ValueSet(ValueSet from)
	{
		addAll(from);
		isSorted = from.isSorted;
	}

	public ValueSet(Value v)
	{
		add(v);
		isSorted = true;
	}

	@Override
	public boolean equals(Object other)
	{
		if (other instanceof ValueSet)
		{
			ValueSet os = (ValueSet) other;
			return os.size() == size() && os.containsAll(this);
		}

		return false;
	}

	@Override
	public int hashCode()
	{
		int hash = 0;

		for (Value v : this)
		{
			hash += v.hashCode();
		}

		return hash;
	}

	@Override
	public boolean add(Value v)
	{
		if (contains(v))
		{
			return true;
		} else
		{
			isSorted = false;
			return super.add(v);
		}
	}

	public boolean addNoCheck(Value v)
	{
		isSorted = false;
		return super.add(v); // Used by power set function
	}

	@Override
	public boolean addAll(Collection<? extends Value> values)
	{
		for (Value v : values)
		{
			add(v);
		}

		return true;
	}

	@Override
	public String toString()
	{
		return Utils.listToString("{", this, ", ", "}");
	}

	public void sort()
	{
		if (!isSorted)
		{
			Collections.sort(this);
			isSorted = true;
		}
	}

	public List<ValueSet> permutedSets()
	{
		// This is a 1st order permutation, which does not take account of the possible
		// nesting of sets or the presence of other permutable values with them (maps).

		List<ValueSet> results = new Vector<>();
		int size = size();

		if (size == 0)
		{
			results.add(new ValueSet()); // Just {}
		} else
		{
			PermuteArray p = new PermuteArray(size);

			while (p.hasNext())
			{
				ValueSet m = new ValueSet();
				int[] perm = p.next();

				for (int i = 0; i < size; i++)
				{
					m.add(get(perm[i]));
				}

				results.add(m);
			}
		}

		return results;
	}

	public List<ValueSet> powerSet()
	{
		List<ValueSet> sets = new Vector<>(2 ^ size());

		if (isEmpty())
		{
			sets.add(new ValueSet()); // Just {}
		} else
		{
			powerGenerate(sets, new boolean[size()], 0);
		}

		return sets;
	}

	private void powerGenerate(List<ValueSet> result, boolean[] flags, int n)
	{
		for (int i = 0; i <= 1; ++i)
		{
			flags[n] = i == 1;

			if (n < flags.length - 1)
			{
				powerGenerate(result, flags, n + 1);
			} else
			{
				ValueSet newset = new ValueSet(flags.length);

				for (int f = 0; f < flags.length; f++)
				{
					if (flags[f])
					{
						newset.addNoCheck(get(f));
					}
				}

				result.add(newset);
			}
		}
	}

	@Override
	public Object clone()
	{
		ValueSet copy = new ValueSet();

		for (Value v : this)
		{
			Value vcopy = (Value) v.clone();
			copy.add(vcopy);
		}

		copy.isSorted = isSorted;
		return copy;
	}
}
