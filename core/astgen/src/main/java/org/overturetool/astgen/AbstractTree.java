/*******************************************************************************
 *
 *	Copyright (c) 2009 Fujitsu Services Ltd.
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

package org.overturetool.astgen;

import java.io.File;
import java.util.List;
import java.util.Vector;

import org.overturetool.vdmj.definitions.ClassDefinition;
import org.overturetool.vdmj.definitions.Definition;
import org.overturetool.vdmj.definitions.TypeDefinition;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.types.NamedType;
import org.overturetool.vdmj.types.RecordType;
import org.overturetool.vdmj.types.Type;

public class AbstractTree
{
	public final String root;
	public final ClassDefinition definition;
	public final String basePkg;
	public final Kind kind;

	private AbstractTree derived;
	public boolean process;

	public AbstractTree(ClassDefinition definition, String basePkg, Kind kind)
	{
		this.root = definition.name.name;
		this.definition = definition;
		this.basePkg = basePkg;
		this.kind = kind;
	}

	public String getPackageDir()
	{
		return basePkg.replace('.', File.separatorChar) +
				File.separator + kind.subpkg;
	}

	public String getPackage()
	{
		return basePkg + "." + kind.subpkg;
	}

	public boolean isDerived()
	{
		return !definition.supernames.isEmpty();
	}

	public void setDerived(AbstractTreeList list)
	{
		derived = list.find(definition.supernames.get(0).name);
	}

	public AbstractTree derivedFrom()
	{
		return derived;
	}

	public List<TypeDefinition> getTypeDefinitions()
	{
		List<TypeDefinition> defs = new Vector<TypeDefinition>();

		for (Definition d: definition.definitions)
		{
			if (d instanceof TypeDefinition)
			{
				defs.add((TypeDefinition)d);
			}
		}

		return defs;
	}

	public String getInheritedTypeName(LexNameToken name, Kind k)
	{
		if (isDerived())
		{
			LexNameToken m = name.getModifiedName(derived.definition.name.name);
			TypeDefinition d = (TypeDefinition)derived.definition.findType(m);

			if (d != null)
			{
				return derived.root + d.name.name + k.extension;
			}
		}

		return null;
	}

	public TypeDefinition findDefinition(Type type)
	{
		if (type instanceof NamedType)
		{
			NamedType nt = (NamedType)type;
			TypeDefinition td = (TypeDefinition)definition.findType(nt.typename);

			if (td == null && derived != null)
			{
				td = derived.findDefinition(type);
			}

			return td;
		}
		else if (type instanceof RecordType)
		{
			RecordType rt = (RecordType)type;
			TypeDefinition td = (TypeDefinition)definition.findType(rt.name);

			if (td == null && derived != null)
			{
				td = derived.findDefinition(type);
			}

			return td;
		}
		else
		{
			System.err.println("Type should be named or record " + type.location);
			System.exit(1);
			return null;
		}
	}

	public String getKindName(LexNameToken name, Kind k)
	{
		return name == null ? null : root + name.name + k.extension;
	}

	public LexNameToken getNodeName()
	{
		return new LexNameToken(root, "Node", new LexLocation());
	}

	public RecordType getSuperClass(LexNameToken name)
	{
		if (isDerived())
		{
			LexNameToken m = name.getModifiedName(derived.definition.name.name);
			TypeDefinition d = (TypeDefinition)derived.definition.findType(m);

			if (d != null)
			{
				return (RecordType)d.type;
			}
		}

		return null;
	}
}
