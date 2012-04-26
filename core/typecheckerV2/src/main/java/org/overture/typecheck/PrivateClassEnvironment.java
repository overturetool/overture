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

import org.overture.ast.definitions.ABusClassDefinition;
import org.overture.ast.definitions.ACpuClassDefinition;
import org.overture.ast.definitions.AStateDefinition;
import org.overture.ast.definitions.ASystemClassDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.definitions.assistants.PDefinitionAssistantTC;
import org.overture.ast.definitions.assistants.SClassDefinitionAssistant;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.typechecker.NameScope;

/**
 * Define the type checking environment for a class as observed from inside.
 */

public class PrivateClassEnvironment extends Environment
{
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
	public PDefinition findName(LexNameToken sought, NameScope scope)
	{
		PDefinition def = SClassDefinitionAssistant.findName(classdef,sought, scope);

		if (def != null)
		{
			return def;
		}

		return (outer == null) ? null : outer.findName(sought, scope);
	}

	@Override
	public PDefinition findType(LexNameToken name, String fromModule)
	{
		PDefinition def = SClassDefinitionAssistant.findType(classdef,name, null);

		if (def != null)
		{
			return def;
		}

		return (outer == null) ? null : outer.findType(name, null);
	}

	@Override
	public Set<PDefinition> findMatches(LexNameToken name)
	{
		Set<PDefinition> defs = SClassDefinitionAssistant.findMatches(classdef,name);

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
