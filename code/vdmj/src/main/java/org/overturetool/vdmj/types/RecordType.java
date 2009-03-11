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

import org.overturetool.vdmj.definitions.TypeDefinition;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.util.Utils;


public class RecordType extends InvariantType
{
	private static final long serialVersionUID = 1L;
	public final LexNameToken name;
	public final List<Field> fields;

	public RecordType(LexNameToken name, List<Field> fields)
	{
		super(name.location);
		this.name = name;
		this.fields = fields;
	}

	public RecordType(LexLocation location, List<Field> fields)
	{
		super(location);
		this.name = new LexNameToken("?", "?", location);
		this.fields = fields;
	}

	public Field findField(String tag)
	{
		for (Field f: fields)
		{
			if (f.tag.equals(tag))
			{
				return f;
			}
		}

		return null;
	}

	@Override
	public Type isType(String typename)
	{
		if (typename.indexOf('`') > 0)
		{
			return (name.getName().equals(typename)) ? this : null;
		}
		else
		{
			// Local typenames aren't qualified with the local module name
			return (name.name.equals(typename)) ? this : null;
		}
	}

	@Override
	public boolean isRecord()
	{
		return true;
	}

	@Override
	public RecordType getRecord()
	{
		return this;
	}

	@Override
	public void unResolve()
	{
		if (!resolved) return; else resolved = false;
		
		for (Field f: fields)
		{
			f.unResolve();
		}
	}

	private boolean infinite = false;

	@Override
	public Type typeResolve(Environment env, TypeDefinition root)
	{
		if (resolved)
		{
			return this;
		}
		else
		{
			resolved = true;
			infinite = false;
		}

		for (Field f: fields)
		{
			if (root != null)
				root.infinite = false;

			f.typeResolve(env, root);

			if (root != null)
				infinite = infinite || root.infinite;
		}

		if (root != null) root.infinite = infinite;
		return this;
	}

	@Override
	public String toDisplay()
	{
		return name.toString();
	}

	@Override
	public String toDetailedString()
	{
		return "compose " + name + " of " + Utils.listToString(fields) + " end";
	}

	@Override
	public boolean equals(Object other)
	{
		while (other instanceof BracketType)
		{
			other = ((BracketType)other).type;
		}

		if (!(other instanceof RecordType))
		{
			return false;
		}

		RecordType cother = (RecordType)other;
		return (name.equals(cother.name) && fields.equals(cother.fields));
	}

	@Override
	public int hashCode()
	{
		return name.hashCode() + fields.hashCode();
	}
}
