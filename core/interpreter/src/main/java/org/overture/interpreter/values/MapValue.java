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

import java.util.Set;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.types.AInMapMapType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SMapType;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.ValueException;

public class MapValue extends Value
{
	private static final long serialVersionUID = 1L;
	public final ValueMap values;

	public MapValue()
	{
		this.values = new ValueMap();
	}

	public MapValue(ValueMap values)
	{
		this.values = values;
	}

	@Override
	public ValueMap mapValue(Context ctxt)
	{
		return values;
	}

	@Override
	public Value getUpdatable(ValueListenerList listeners)
	{
		ValueMap nm = new ValueMap();

		for (Value k : values.keySet())
		{
			Value v = values.get(k).getUpdatable(listeners);
			nm.put(k, v);
		}

		return UpdatableValue.factory(new MapValue(nm), listeners);
	}

	@Override
	public Value getConstant()
	{
		ValueMap nm = new ValueMap();

		for (Value k : values.keySet())
		{
			Value v = values.get(k).getConstant();
			nm.put(k, v);
		}

		return new MapValue(nm);
	}

	public Value lookup(Value arg, Context ctxt) throws ValueException
	{
		Value v = values.get(arg);

		if (v == null)
		{
			abort(4061, "No such key value in map: " + arg, ctxt);
		}

		return v;
	}

	@Override
	public boolean equals(Object other)
	{
		if (other instanceof Value)
		{
			Value val = ((Value) other).deref();

			if (val instanceof MapValue)
			{
				MapValue ot = (MapValue) val;
				return values.equals(ot.values);
			}
		}

		return false;
	}

	@Override
	public String toString()
	{
		return values.isEmpty() ? "{|->}" : values.toString();
	}

	@Override
	public int hashCode()
	{
		return values.hashCode();
	}

	@Override
	public String kind()
	{
		return "map";
	}

	@Override
	protected Value convertValueTo(PType to, Context ctxt, Set<PType> done)
			throws AnalysisException
	{
		if (to instanceof SMapType)
		{
			if (to instanceof AInMapMapType && !values.isInjective())
			{
				abort(4062, "Cannot convert non-injective map to an inmap", ctxt);
			}

			SMapType mapto = ctxt.assistantFactory.createPTypeAssistant().getMap(to, null);
			ValueMap nm = new ValueMap();

			for (Value k : values.keySet())
			{
				Value v = values.get(k);
				Value dom = k.convertValueTo(mapto.getFrom(), ctxt);
				Value rng = v.convertValueTo(mapto.getTo(), ctxt);

				Value old = nm.put(dom, rng);

				if (old != null && !old.equals(rng))
				{
					abort(4063, "Duplicate map keys have different values: "
							+ dom, ctxt);
				}
			}

			return new MapValue(nm);
		} else
		{
			return super.convertValueTo(to, ctxt, done);
		}
	}

	@Override
	public Object clone()
	{
		return new MapValue((ValueMap) values.clone());
	}
}
