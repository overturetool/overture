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

package org.overture.typecheck;

import java.util.Set;

import org.overture.ast.definitions.AStateDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.definitions.assistants.PDefinitionAssistant;
import org.overture.ast.definitions.assistants.PDefinitionListAssistant;
import org.overture.ast.modules.AModuleModules;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.typechecker.NameScope;




/**
 * Define the type checking environment for a modular specification.
 */

public class ModuleEnvironment extends Environment
{
	private final AModuleModules module;

	public ModuleEnvironment(AModuleModules module)
	{
		super(null);
		this.module = module;
		dupHideCheck(module.getDefs(), NameScope.NAMESANDSTATE);
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();

		for (PDefinition d: module.getDefs())
		{
			sb.append(d.getName().getName());
			sb.append("\n");
		}

		return sb.toString();
	}

	@Override
	public PDefinition findName(LexNameToken name, NameScope scope)
	{
		PDefinition def = PDefinitionListAssistant.findName(module.getDefs(),name, scope);

		if (def != null)
		{
			return def;
		}

		def = PDefinitionListAssistant.findName(module.getImportdefs(),name, scope);

		if (def != null)
		{
			return def;
		}

   		return null;	// Modules are always bottom of the env chain
	}

	@Override
	public PDefinition findType(LexNameToken name, String fromModule)
	{
		PDefinition def = PDefinitionAssistant.findType(module.getDefs(), name,module.getName().getName());

		if (def != null)
		{
			return def.clone();
		}

		def =  PDefinitionAssistant.findType(module.getImportdefs(),name,module.getName().getName());

		if (def != null)
		{
			return def.clone();
		}

		return null;	// Modules are always bottom of the env chain
	}

	@Override
	public Set<PDefinition> findMatches(LexNameToken name)
	{
		Set<PDefinition> defs = PDefinitionListAssistant.findMatches(module.getDefs(),name);
		defs.addAll(PDefinitionListAssistant.findMatches(module.getImportdefs(),name));
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
	public AStateDefinition findStateDefinition()
	{
		AStateDefinition def = PDefinitionListAssistant.findStateDefinition(module.getDefs());

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
	public SClassDefinition findClassDefinition()
	{
		return null;
	}

	@Override
	public boolean isStatic()
	{
		return false;
	}
}
