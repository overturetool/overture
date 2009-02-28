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

import java.util.List;

import org.overturetool.vdmj.definitions.DefinitionList;
import org.overturetool.vdmj.lex.LexIdentifierToken;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.TypeChecker;


public class ModuleImports
{
	public final LexIdentifierToken name;
	public final List<ImportFromModule> imports;

	public ModuleImports(LexIdentifierToken name, List<ImportFromModule> imports)
	{
		this.name = name;
		this.imports = imports;
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();

		for (ImportFromModule def: imports)
		{
			sb.append(def.toString());
			sb.append("\n");
		}

		return sb.toString();
	}

	public DefinitionList getDefinitions(ModuleList allModules)
	{
		DefinitionList defs = new DefinitionList();

		for (ImportFromModule ifm: imports)
		{
			if (ifm.name.equals(name))
			{
				TypeChecker.report(3195, "Cannot import from self", ifm.name.location);
				continue;
			}

			Module from = allModules.findModule(ifm.name);

			if (from == null)
			{
				TypeChecker.report(3196, "No such module as " + ifm.name, ifm.name.location);
			}
			else
			{
				defs.addAll(ifm.getDefinitions(from));
			}
		}

		return defs;
	}

	public void typeCheck(Environment env)
	{
		for (ImportFromModule ifm: imports)
		{
			ifm.typeCheck(env);
		}
	}
}
