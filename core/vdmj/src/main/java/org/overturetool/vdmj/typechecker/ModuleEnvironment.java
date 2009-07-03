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

package org.overturetool.vdmj.typechecker;

import org.overturetool.vdmj.definitions.ClassDefinition;
import org.overturetool.vdmj.definitions.Definition;
import org.overturetool.vdmj.definitions.DefinitionSet;
import org.overturetool.vdmj.definitions.StateDefinition;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.modules.Module;

/**
 * Define the type checking environment for a modular specification.
 */

public class ModuleEnvironment extends Environment
{
	private final Module module;

	public ModuleEnvironment(Module module)
	{
		super(null);
		this.module = module;
		dupHideCheck(module.defs);
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();

		for (Definition d: module.defs)
		{
			sb.append(d.name);
			sb.append("\n");
		}

		return sb.toString();
	}

	@Override
	public Definition findName(LexNameToken name, NameScope scope)
	{
		Definition def = module.defs.findName(name, scope);

		if (def != null)
		{
			return def;
		}

		def = module.importdefs.findName(name, scope);

		if (def != null)
		{
			return def;
		}

   		return null;	// Modules are always bottom of the env chain
	}

	@Override
	public Definition findType(LexNameToken name)
	{
		Definition def = module.defs.findType(name);

		if (def != null)
		{
			return def;
		}

		def = module.importdefs.findType(name);

		if (def != null)
		{
			return def;
		}

		return null;	// Modules are always bottom of the env chain
	}

	@Override
	public DefinitionSet findMatches(LexNameToken name)
	{
		DefinitionSet defs = module.defs.findMatches(name);
		defs.addAll(module.importdefs.findMatches(name));
		return defs;
	}

	@Override
	public void unusedCheck()
	{
		// The usage of all modules is checked at the end of the type check
		// phase. Only flat environments implement this check, for unused
		// local definitions introduced by expressions and statements.
	}

	@Override
	public StateDefinition findStateDefinition()
	{
		StateDefinition def = module.defs.findStateDefinition();

		if (def != null)
		{
			return def;
		}

		return null;	// Modules are always bottom of the env chain
	}

	@Override
	public boolean isVDMPP()
	{
		return false;
	}

	@Override
	public boolean isSystem()
	{
		return false;
	}

	@Override
	public ClassDefinition findClassDefinition()
	{
		return null;
	}

	@Override
	public boolean isStatic()
	{
		return false;
	}
}
