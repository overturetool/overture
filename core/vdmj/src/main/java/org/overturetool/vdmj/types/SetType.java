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
import org.overturetool.vdmj.values.SetValue;
import org.overturetool.vdmj.values.ValueList;
import org.overturetool.vdmj.values.ValueSet;

public class SetType extends Type
{
	private static final long serialVersionUID = 1L;
	public Type setof;
	public final boolean empty;

	public SetType(LexLocation location, Type type)
	{
		super(location);
		this.setof = type;
		this.empty = false;
	}

	public SetType(LexLocation location)
	{
		super(location);
		this.setof = new UnknownType(location);
		this.empty = true;
	}

	@Override
	public boolean narrowerThan(AccessSpecifier accessSpecifier)
	{
		return setof.narrowerThan(accessSpecifier);
	}

	@Override
	public boolean isSet()
	{
		return true;
	}

	@Override
	public SetType getSet()
	{
		return this;
	}

	@Override
	public void unResolve()
	{
		if (!resolved) return; else { resolved = false; }
		setof.unResolve();
	}

	@Override
	public Type typeResolve(Environment env, TypeDefinition root)
	{
		if (resolved) return this; else { resolved = true; }

		try
		{
			setof = setof.typeResolve(env, root);
			if (root != null) root.infinite = false;	// Could be empty
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
		return new SetType(location, setof.polymorph(pname, actualType));
	}

	@Override
	public String toDisplay()
	{
		return empty ? "{}" : "set of (" + setof + ")";
	}

	@Override
	public boolean equals(Object other)
	{
		other = deBracket(other);

		if (other instanceof SetType)
		{
			SetType os = (SetType)other;
			// NB empty set same type as any set
			return empty || os.empty || setof.equals(os.setof);
		}

		return false;
	}

	@Override
	public int hashCode()
	{
		return empty ? 0 : setof.hashCode();
	}

	@Override
	public ValueList getAllValues(Context ctxt) throws ValueException
	{
		ValueList list = setof.getAllValues(ctxt);
		ValueSet set = new ValueSet(list.size());
		set.addAll(list);
		List<ValueSet> psets = set.powerSet();
		list.clear();

		for (ValueSet v: psets)
		{
			list.add(new SetValue(v));
		}

		return list;
	}
}
