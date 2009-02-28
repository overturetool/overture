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
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.typechecker.TypeComparator;
import org.overturetool.vdmj.types.Type;

public class ImportedValue extends Import
{
	public Type type;

	public ImportedValue(LexNameToken name, Type type, LexNameToken renamed)
	{
		super(name, renamed);
		this.type = type;
	}

	@Override
	public DefinitionList getDefinitions(Module module)
	{
		DefinitionList list = new DefinitionList();
		from = module;
		Definition expdef = from.exportdefs.findName(name, NameScope.NAMES);

		if (expdef == null)
		{
			report(3193, "No export declared for import of value " + name + " from " + from.name);
		}
		else
		{
			if (renamed != null)
			{
				expdef = new RenamedDefinition(renamed, expdef, NameScope.GLOBAL);
			}
			else
			{
				expdef = new ImportedDefinition(name.location, expdef, NameScope.GLOBAL);
			}

			list.add(expdef);
		}

		return list;
	}

	@Override
	public String toString()
	{
		return "import value " +
				(renamed == null ? "" : " renamed " + renamed.name) +
				(type == null ? "" : ":" + type);
	}

	@Override
	public void typeCheck(Environment env)
	{
		if (type != null && from != null)
		{
			type = type.typeResolve(env, null);
			Definition expdef = from.exportdefs.findName(name, NameScope.NAMES);

			if (expdef != null)
			{
    			Type exptype = expdef.getType().typeResolve(env, null);

    			if (!TypeComparator.compatible(type, exptype))
    			{
    				report(3194, "Type of value import " + name + " does not match export from " + from.name);
    				detail2("Import", type.toDetailedString(), "Export", exptype.toDetailedString());
    			}
			}
		}
	}
}
