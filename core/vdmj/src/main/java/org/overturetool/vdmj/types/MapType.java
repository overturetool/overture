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

import org.overturetool.vdmj.definitions.AccessSpecifier;
import org.overturetool.vdmj.definitions.TypeDefinition;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.TypeCheckException;

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
		while (other instanceof BracketType)
		{
			other = ((BracketType)other).type;
		}

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
}
