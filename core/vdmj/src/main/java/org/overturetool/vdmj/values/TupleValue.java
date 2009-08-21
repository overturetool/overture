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

import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.ValueException;
import org.overturetool.vdmj.types.ProductType;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.util.Utils;


public class TupleValue extends Value
{
	private static final long serialVersionUID = 1L;
	public final ValueList values;

	public TupleValue(ValueList argvals)
	{
		this.values = argvals;
	}

	@Override
	public ValueList tupleValue(Context ctxt)
	{
		return values;
	}

	@Override
	public Value getUpdatable(ValueListener listener)
	{
		ValueList ntup = new ValueList();

		for (Value k: values)
		{
			Value v = k.getUpdatable(listener);
			ntup.add(v);
		}

		return UpdatableValue.factory(new TupleValue(ntup), listener);
	}

	@Override
	public boolean equals(Object other)
	{
		if (other instanceof Value)
		{
			Value val = ((Value)other).deref();

    		if (val instanceof TupleValue)
    		{
    			TupleValue ot = (TupleValue)val;
    			return values.equals(ot.values);
    		}
		}

		return false;
	}

	@Override
	public String toString()
	{
		return "mk_(" + Utils.listToString(values) + ")";
	}

	@Override
	public int hashCode()
	{
		return values.hashCode();
	}

	@Override
	public String kind()
	{
		return "tuple";
	}

	@Override
	public Value convertValueTo(Type to, Context ctxt) throws ValueException
	{
		if (to instanceof ProductType)
		{
			ProductType pto = (ProductType)to;

			if (pto.types.size() != values.size())
			{
				abort(4085, "Cannot convert tuple to " + to, ctxt);
			}

			ValueList nl = new ValueList();
			Iterator<Value> vi = values.iterator();

			for (Type pt: pto.types)
			{
				nl.add(vi.next().convertValueTo(pt, ctxt));
			}

			return new TupleValue(nl);
		}
		else
		{
			return super.convertValueTo(to, ctxt);
		}
	}

	@Override
	public Object clone()
	{
		return new TupleValue((ValueList)values.clone());
	}
}
