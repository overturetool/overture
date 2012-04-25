/*******************************************************************************
 *
 *	Copyright (c) 2008 Fujitsu Services Ltd.
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

package org.overturetool.vdmj.types;

import java.util.List;

import org.overturetool.vdmj.definitions.AccessSpecifier;
import org.overturetool.vdmj.definitions.TypeDefinition;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.ValueException;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.TypeCheckException;
import org.overturetool.vdmj.values.MapValue;
import org.overturetool.vdmj.values.TupleValue;
import org.overturetool.vdmj.values.Value;
import org.overturetool.vdmj.values.ValueList;
import org.overturetool.vdmj.values.ValueMap;
import org.overturetool.vdmj.values.ValueSet;

public class MapType extends Type
{
	private static final long serialVersionUID = 1L;
	public Type from;
	public Type to;
	public final boolean empty;

	public MapType(LexLocation location, Type from, Type to)
	{
		super(location);
		this.from = from;
		this.to = to;
		this.empty = false;
	}

	public MapType(LexLocation location)
	{
		super(location);
		this.from = new UnknownType(location);
		this.to = new UnknownType(location);
		this.empty = true;
	}

	@Override
	public boolean narrowerThan(AccessSpecifier accessSpecifier)
	{
		return from.narrowerThan(accessSpecifier) ||
				to.narrowerThan(accessSpecifier);
	}

	@Override
	public boolean isMap()
	{
		return true;
	}

	@Override
	public MapType getMap()
	{
		return this;
	}

	@Override
	public void unResolve()
	{
		if (!resolved) return; else { resolved = false; }

		if (!empty)
		{
			from.unResolve();
			to.unResolve();
		}
	}

	@Override
	public MapType typeResolve(Environment env, TypeDefinition root)
	{
		if (resolved) return this; else { resolved = true; }

		try
		{
			if (!empty)
			{
				from = from.typeResolve(env, root);
				to = to.typeResolve(env, root);
			}

			return this;
		}
		catch (TypeCheckException e)
		{
			unResolve();
			throw e;
		}
	}

	@Override
	public Type polymorph(LexNameToken pname, Type actualType)
	{
		return new MapType(location,
			from.polymorph(pname, actualType), to.polymorph(pname, actualType));
	}

	@Override
	public String toDisplay()
	{
		return "map (" + from + ") to (" + to + ")";
	}

	@Override
	public boolean equals(Object other)
	{
		other = deBracket(other);

		if (other instanceof MapType)
		{
			MapType mt = (MapType)other;
			return from.equals(mt.from) && to.equals(mt.to);
		}

		return false;
	}

	@Override
	public int hashCode()
	{
		return from.hashCode() + to.hashCode();
	}
	
	@Override
	public ValueList getAllValues(Context ctxt) throws ValueException
	{
		TypeList tuple = new TypeList();
		tuple.add(from);
		tuple.add(to);
		
		ValueList results = new ValueList();
		ValueList tuples = tuple.getAllValues(ctxt);
		ValueSet set = new ValueSet();
		set.addAll(tuples);
		List<ValueSet> psets = set.powerSet();

		for (ValueSet map: psets)
		{
			ValueMap result = new ValueMap();
			
			for (Value v: map)
			{
				TupleValue tv = (TupleValue)v;
				result.put(tv.values.get(0), tv.values.get(1));
			}
			
			results.add(new MapValue(result));
		}
		
		return results; 
	}
}
