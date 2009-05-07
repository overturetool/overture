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

package org.overturetool.vdmj.modules;

import org.overturetool.vdmj.definitions.Definition;
import org.overturetool.vdmj.definitions.DefinitionList;
import org.overturetool.vdmj.definitions.TypeDefinition;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.types.NamedType;
import org.overturetool.vdmj.types.UnknownType;

public class ExportedType extends Export
{
	private static final long serialVersionUID = 1L;
	public final LexNameToken name;
	public final boolean struct;

	public ExportedType(LexNameToken name, boolean struct)
	{
		super(name.location);
		this.name = name;
		this.struct = struct;
	}

	@Override
	public String toString()
	{
		return "export type " + (struct ? "struct " : "") + name.name;
	}

	@Override
	public DefinitionList getDefinition(DefinitionList actualDefs)
	{
		DefinitionList list = new DefinitionList();
		Definition def = actualDefs.findType(name);

		if (def == null)
		{
			report(3187, "Exported type " + name + " not defined in module");
		}
		else
		{
			if (struct)
			{
				list.add(def);
			}
			else
			{
				// Structureless type
				list.add(new TypeDefinition(def.name,
					new NamedType(def.name, new UnknownType(def.location)),
					null, null));
			}
		}

		return list;
	}

	@Override
	public DefinitionList getDefinition()
	{
		return new DefinitionList();
	}
}
