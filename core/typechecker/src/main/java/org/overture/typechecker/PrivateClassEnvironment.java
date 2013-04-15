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

import org.overture.ast.definitions.ABusClassDefinition;
import org.overture.ast.definitions.ACpuClassDefinition;
import org.overture.ast.definitions.AStateDefinition;
import org.overture.ast.definitions.ASystemClassDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.typechecker.NameScope;
import org.overture.typechecker.assistant.definition.PDefinitionAssistantTC;
import org.overture.typechecker.assistant.definition.SClassDefinitionAssistantTC;

/**
 * Define the type checking environment for a class as observed from inside.
 */

public class PrivateClassEnvironment extends Environment
{
	public List<PDefinition> getDefinitions()
	{
		return classdef.getDefinitions();
	}
	private final SClassDefinition classdef;

	public PrivateClassEnvironment(SClassDefinition classdef)
	{
		this(classdef, null);
	}

	public PrivateClassEnvironment(SClassDefinition classdef, Environment env)
	{
		super(env);
		this.classdef = classdef;
	}

	@Override
	public PDefinition findName(ILexNameToken sought, NameScope scope)
	{
		PDefinition def = SClassDefinitionAssistantTC.findName(classdef,sought, scope);

		if (def != null)
		{
			return def;
		}

		return (outer == null) ? null : outer.findName(sought, scope);
	}

	@Override
	public PDefinition findType(ILexNameToken name, String fromModule)
	{
		PDefinition def = SClassDefinitionAssistantTC.findType(classdef,name, null);

		if (def != null)
		{
			return def;
		}

		return (outer == null) ? null : outer.findType(name, null);
	}

	@Override
	public Set<PDefinition> findMatches(ILexNameToken name)
	{
		Set<PDefinition> defs = SClassDefinitionAssistantTC.findMatches(classdef,name);

		if (outer != null)
		{
			defs.addAll(outer.findMatches(name));
		}

		return defs;
	}

	@Override
	public void unusedCheck()
	{
		PDefinitionAssistantTC.unusedCheck(classdef);
	}

	@Override
	public AStateDefinition findStateDefinition()
	{
		return null;
	}

	@Override
	public boolean isVDMPP()
	{
		return true;
	}

	@Override
	public boolean isSystem()
	{
		return (classdef instanceof ASystemClassDefinition ||
				classdef instanceof ACpuClassDefinition ||
				classdef instanceof ABusClassDefinition);
	}

	@Override
	public SClassDefinition findClassDefinition()
	{
		return classdef;
	}

	@Override
	public boolean isStatic()
	{
		return false;
	}
}
