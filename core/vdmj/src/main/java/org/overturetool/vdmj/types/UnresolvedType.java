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

import org.overturetool.vdmj.definitions.ClassDefinition;
import org.overturetool.vdmj.definitions.Definition;
import org.overturetool.vdmj.definitions.DefinitionList;
import org.overturetool.vdmj.definitions.ImportedDefinition;
import org.overturetool.vdmj.definitions.InheritedDefinition;
import org.overturetool.vdmj.definitions.RenamedDefinition;
import org.overturetool.vdmj.definitions.StateDefinition;
import org.overturetool.vdmj.definitions.TypeDefinition;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.TypeCheckException;

public class UnresolvedType extends Type
{
	private static final long serialVersionUID = 1L;
	public final LexNameToken typename;

	public UnresolvedType(LexNameToken typename)
	{
		super(typename.location);
		this.typename = typename;
	}

	@Override
	public Type isType(String other)
	{
		return typename.getName().equals(other) ? this : null;
	}

	@Override
	public Type typeResolve(Environment env, TypeDefinition root)
	{
		Type deref = dereference(env, root);

		if (!(deref instanceof ClassType))
		{
			deref = deref.typeResolve(env, root);
		}

		return deref;
	}

	private Type dereference(Environment env, TypeDefinition root)
	{
		Definition def = env.findType(typename);

		if (def == null)
		{
			throw new TypeCheckException(
				"Unable to resolve type name '" + typename + "'", location);
		}

		if (def instanceof ImportedDefinition)
		{
			ImportedDefinition idef = (ImportedDefinition)def;
			def = idef.def;
		}

		if (def instanceof RenamedDefinition)
		{
			RenamedDefinition rdef = (RenamedDefinition)def;
			def = rdef.def;
		}

		if (!(def instanceof TypeDefinition) &&
			!(def instanceof StateDefinition) &&
			!(def instanceof ClassDefinition) &&
			!(def instanceof InheritedDefinition))
		{
			report(3434, "'" + typename + "' is not the name of a type definition");
		}

		if (def instanceof TypeDefinition)
		{
			if (def == root)
			{
				root.infinite = true;
			}
		}

		Type r = def.getType();
		r.definitions = new DefinitionList(def);
		return r;
	}

	@Override
	public boolean equals(Object other)
	{
		while (other instanceof BracketType)
		{
			other = ((BracketType)other).type;
		}

		if (other instanceof UnresolvedType)
		{
			UnresolvedType nother = (UnresolvedType)other;
			return typename.equals(nother.typename);
		}

		if (other instanceof NamedType)
		{
			NamedType nother = (NamedType)other;
			return typename.equals(nother.typename);
		}

		return false;
	}

	@Override
	public int hashCode()
	{
		return typename.hashCode();
	}

	@Override
	public String toDisplay()
	{
		return "(unresolved " + typename + ")";
	}
}
