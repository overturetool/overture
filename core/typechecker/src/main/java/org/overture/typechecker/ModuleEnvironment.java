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

package org.overture.typechecker;

import java.util.List;
import java.util.Set;

import org.overture.ast.definitions.AStateDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.typechecker.NameScope;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

/**
 * Define the type checking environment for a modular specification.
 */

public class ModuleEnvironment extends Environment
{
	private final AModuleModules module;

	public List<PDefinition> getDefinitions()
	{
		return module.getDefs();
	}

	public ModuleEnvironment(ITypeCheckerAssistantFactory af,
			AModuleModules module)
	{
		super(af, null);
		this.module = module;
		dupHideCheck(module.getDefs(), NameScope.NAMESANDSTATE);
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();

		for (PDefinition d : module.getDefs())
		{
			sb.append(d.getName().getFullName());
			sb.append("\n");
		}

		return sb.toString();
	}

	@Override
	public PDefinition findName(ILexNameToken name, NameScope scope)
	{
		PDefinition def = af.createPDefinitionListAssistant().findName(module.getDefs(), name, scope);

		if (def != null && !ExcludedDefinitions.isExcluded(def))
		{
			return def;
		}

		def = af.createPDefinitionListAssistant().findName(module.getImportdefs(), name, scope);

		if (def != null)
		{
			return def;
		}

		return null; // Modules are always bottom of the env chain
	}

	@Override
	public PDefinition findType(ILexNameToken name, String fromModule)
	{
		PDefinition def = af.createPDefinitionAssistant().findType(module.getDefs(), name, fromModule);

		if (def != null)
		{
			return def;
		}

		def = af.createPDefinitionAssistant().findType(module.getImportdefs(), name, fromModule);

		if (def != null)
		{
			return def;
		}

		return null; // Modules are always bottom of the env chain
	}

	@Override
	public Set<PDefinition> findMatches(ILexNameToken name)
	{
		Set<PDefinition> defs = af.createPDefinitionListAssistant().findMatches(module.getDefs(), name);
		defs.addAll(af.createPDefinitionListAssistant().findMatches(module.getImportdefs(), name));
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
		AStateDefinition def = af.createPDefinitionListAssistant().findStateDefinition(module.getDefs());

		if (def != null)
		{
			return def;
		}

		return null; // Modules are always bottom of the env chain
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
