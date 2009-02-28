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

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import org.overturetool.vdmj.util.Utils;


/**
 * A set of values. Note that although this class implements a set (no duplicates)
 * it is not based on a java.util.Set<Value>, but rather a Vector<Value>. This is
 * so that the possible orderings of set values can be enumerated when
 * performing quantifiers like "a,b,c in set {{1,2,3}, {4,5,6}}".
 */

@SuppressWarnings("serial")
public class ValueSet extends Vector<Value>		// NB based on Vector
{
	public ValueSet()
	{
		super();
	}

	public ValueSet(int size)
	{
		super(size);
	}

	public ValueSet(ValueSet from)
	{
		addAll(from);
	}

	public ValueSet(Value v)
	{
		add(v);
	}

	@Override
	public boolean equals(Object other)
	{
		if (other instanceof ValueSet)
		{
			ValueSet os = (ValueSet)other;
			return os.size() == size() && os.containsAll(this);
		}

		return false;
	}

	@Override
	public int hashCode()
	{
		int hash = 0;

		for (Value v: this)
		{
			hash += v.hashCode();
		}

		return hash;
	}

	@Override
	public boolean add(Value v)
	{
		return contains(v) ? true : super.add(v);
	}

	public boolean addNoCheck(Value v)
	{
		return super.add(v);	// Used by power set function
	}

	@Override
	public boolean addAll(Collection<? extends Value> values)
	{
		for (Value v: values)
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

	public ValueSet sorted()
	{
		ValueSet copy = new ValueSet();

		for (Value v: this)
		{
			copy.add(v.sorted());
		}

		Collections.sort(copy);
		return copy;
	}

	// BEWARE: This can generate a lot of sets if there are sets with
	// sub-subsets etc.

	public List<ValueSet> permutedSets()
	{
		List<ValueSet> results = new Vector<ValueSet>();

		if (size() == 0)
		{
			results.add(new ValueSet());	// Just {}
		}
		else if (size() == 1)
		{
			Value element = get(0).deref();

			if (element instanceof SetValue)
			{
				ValueSet subset = ((SetValue)element).values;

    			for (ValueSet vs: subset.permutedSets())
    			{
    				results.add(new ValueSet(new SetValue(vs)));
    			}
			}
			else
			{
				results.add(new ValueSet(this));
			}
		}
		else
		{
    		for (Value element: this)
    		{
    			ValueSet remainder = new ValueSet(this);
    			remainder.remove(element);

    			if (element instanceof SetValue)
    			{
    				ValueSet subset = ((SetValue)element).values;

    				for (ValueSet ss: subset.permutedSets())
    				{
            			for (ValueSet vs: remainder.permutedSets())
            			{
            				vs.add(new SetValue(ss));
            				results.add(vs);
            			}
    				}
    			}
    			else
    			{
        			for (ValueSet vs: remainder.permutedSets())
        			{
        				vs.add(element);
        				results.add(vs);
        			}
    			}
    		}
		}

		return results;
	}

	public List<ValueSet> powerSet()
	{
		List<ValueSet> sets = new Vector<ValueSet>(2^size());

		if (isEmpty())
		{
			sets.add(new ValueSet());	// Just {}
		}
		else
		{
			powerGenerate(sets, new boolean[size()], 0);
		}

		return sets;
	}

	private void powerGenerate(List<ValueSet> result, boolean[] flags, int n)
	{
		for (int i=0; i <= 1; ++i)
		{
			flags[n] = (i == 1);

			if (n < flags.length - 1)
			{
				powerGenerate(result, flags, n+1);
			}
			else
			{
				ValueSet newset = new ValueSet(flags.length);

				for (int f=0; f<flags.length; f++)
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

		for (Value v: this)
		{
			Value vcopy = (Value)v.clone();
			copy.add(vcopy);
		}

		return copy;
	}
}
