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
import org.overturetool.vdmj.definitions.ImportedDefinition;
import org.overturetool.vdmj.definitions.RenamedDefinition;
import org.overturetool.vdmj.definitions.TypeDefinition;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.typechecker.TypeComparator;
import org.overturetool.vdmj.types.Type;

public class ImportedType extends Import
{
	private static final long serialVersionUID = 1L;
	public final TypeDefinition def;

	public ImportedType(LexNameToken name, LexNameToken renamed)
	{
		super(name, renamed);
		this.def = null;
	}

	public ImportedType(TypeDefinition def, LexNameToken renamed)
	{
		super(def.name, renamed);
		this.def = def;
	}

	@Override
	public String toString()
	{
		return "import type " +
				(def == null ? name.name : def.toString()) +
				(renamed == null ? "" : " renamed " + renamed.name);
	}

	@Override
	public DefinitionList getDefinitions(Module module)
	{
		DefinitionList list = new DefinitionList();
		from = module;
		Definition expdef = from.exportdefs.findType(name);

		if (expdef == null)
		{
			report(3191, "No export declared for import of type " + name + " from " + from.name);
		}
		else
		{
			if (renamed != null)
			{
				expdef = new RenamedDefinition(renamed, expdef, NameScope.TYPENAME);
			}
			else
			{
				expdef = new ImportedDefinition(
								name.location, expdef, NameScope.TYPENAME);
			}

			list.add(expdef);
		}

		return list;
	}

	@Override
	public void typeCheck(Environment env)
	{
		if (def != null && from != null)
		{
			def.type = def.type.typeResolve(env, null);
			Definition expdef = from.exportdefs.findType(name);

			if (expdef != null)
			{
				Type exptype = expdef.getType().typeResolve(env, null);

				if (!TypeComparator.compatible(def.type, exptype))
				{
					report(3192, "Type import of " + name + " does not match export from " + from.name);
					detail2("Import", def.type.toDetailedString(), "Export", exptype.toDetailedString());
				}
			}
		}
	}
}
