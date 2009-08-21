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
import org.overturetool.vdmj.types.Field;
import org.overturetool.vdmj.types.RecordType;
import org.overturetool.vdmj.types.Type;


public class RecordValue extends Value
{
	private static final long serialVersionUID = 1L;
	public final RecordType type;
	public final FieldMap fieldmap;
	public final FunctionValue invariant;

	// mk_ expressions
	public RecordValue(RecordType type,	ValueList values, Context ctxt)
		throws ValueException
	{
		this.type = type;
		this.fieldmap = new FieldMap();
		this.invariant = type.getInvariant(ctxt);

		if (values.size() != type.fields.size())
		{
			abort(4078, "Wrong number of fields for " + type.name, ctxt);
		}

		Iterator<Field> fi = type.fields.iterator();

		for (Value v: values)
		{
			Field f = fi.next();
			fieldmap.add(f.tag, v.convertValueTo(f.type, ctxt), !f.equalityAbstration);
		}

		if (invariant != null &&
			!invariant.eval(invariant.location, this, ctxt).boolValue(ctxt))
		{
			abort(4079, "Type invariant violated by mk_ arguments", ctxt);
		}
	}

	// mu_ expressions
	public RecordValue(RecordType type,	FieldMap mapvalues, Context ctxt)
		throws ValueException
	{
		this.type = type;
		this.fieldmap = new FieldMap();
		this.invariant = type.getInvariant(ctxt);

		if (mapvalues.size() != type.fields.size())
		{
			abort(4080, "Wrong number of fields for " + type.name, ctxt);
		}

		Iterator<Field> fi = type.fields.iterator();

		while (fi.hasNext())
		{
			Field f = fi.next();
			Value v = mapvalues.get(f.tag);

			if (v == null)
			{
				abort(4081, "Field not defined: " + f.tag, ctxt);
			}

			fieldmap.add(f.tag, v.convertValueTo(f.type, ctxt), !f.equalityAbstration);
		}

		if (invariant != null &&
			!invariant.eval(invariant.location, this, ctxt).boolValue(ctxt))
		{
			abort(4082, "Type invariant violated by mk_ arguments", ctxt);
		}
	}

	// Only called by clone()
	private RecordValue(RecordType type, FieldMap mapvalues, FunctionValue invariant)
	{
		this.type = type;
		this.invariant = invariant;
		this.fieldmap = mapvalues;
	}

	// State records - invariant handled separately
	public RecordValue(RecordType type, NameValuePairList mapvalues)
	{
		this.type = type;
		this.invariant = null;
		this.fieldmap = new FieldMap();

		for (NameValuePair nvp: mapvalues)
		{
			Field f = type.findField(nvp.name.name);
			this.fieldmap.add(nvp.name.name, nvp.value, !f.equalityAbstration);
		}
	}

	@Override
	public RecordValue recordValue(Context ctxt)
	{
		return this;
	}

	@Override
	public Value getUpdatable(ValueListener listener)
	{
		FieldMap nm = new FieldMap();

		for (FieldValue fv: fieldmap)
		{
			Value uv = fv.value.getUpdatable(listener);
			nm.add(fv.name, uv, fv.comparable);
		}

		return UpdatableValue.factory(new RecordValue(type, nm, invariant), listener);
	}

	@Override
	public boolean equals(Object other)
	{
		if (other instanceof Value)
		{
			Value val = ((Value)other).deref();

    		if (val instanceof RecordValue)
    		{
    			RecordValue ot = (RecordValue)val;

    			if (ot.type.equals(type))
    			{
    				for (Field f: type.fields)
    				{
    					if (!f.equalityAbstration)
    					{
    						Value fv = fieldmap.get(f.tag);
    						Value ofv = ot.fieldmap.get(f.tag);

    						if (fv == null || ofv == null || !fv.equals(ofv))
    						{
    							return false;
    						}
    					}
    				}

    				return true;
    			}
    		}
		}

		return false;
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("mk_" + type.name + "(");

		Iterator<Field> fi = type.fields.iterator();

		if (fi.hasNext())
		{
    		String ftag = fi.next().tag;
    		sb.append(fieldmap.get(ftag));

    		while (fi.hasNext())
    		{
    			ftag = fi.next().tag;
    			sb.append(", " + fieldmap.get(ftag));
    		}
		}

		sb.append(")");
		return sb.toString();
	}

	@Override
	public int hashCode()
	{
		return type.name.hashCode() + fieldmap.hashCode();
	}

	@Override
	public String kind()
	{
		return type.toString();
	}

	@Override
	public Value convertValueTo(Type to, Context ctxt) throws ValueException
	{
		if (to.equals(type))
		{
			return this;
		}
		else
		{
			return super.convertValueTo(to, ctxt);
		}
	}

	@Override
	public Object clone()
	{
		return new RecordValue(type, (FieldMap)fieldmap.clone(), invariant);
	}
}
