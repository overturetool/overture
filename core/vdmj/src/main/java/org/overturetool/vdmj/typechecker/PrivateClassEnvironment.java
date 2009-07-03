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

import org.overturetool.vdmj.definitions.BUSClassDefinition;
import org.overturetool.vdmj.definitions.CPUClassDefinition;
import org.overturetool.vdmj.definitions.ClassDefinition;
import org.overturetool.vdmj.definitions.Definition;
import org.overturetool.vdmj.definitions.DefinitionSet;
import org.overturetool.vdmj.definitions.StateDefinition;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.syntax.SystemDefinition;

/**
 * Define the type checking environment for a class as observed from inside.
 */

public class PrivateClassEnvironment extends Environment
{
	private final ClassDefinition classdef;

	public PrivateClassEnvironment(ClassDefinition classdef)
	{
		this(classdef, null);
	}

	public PrivateClassEnvironment(ClassDefinition classdef, Environment env)
	{
		super(env);
		this.classdef = classdef;
	}

	@Override
	public Definition findName(LexNameToken sought, NameScope scope)
	{
		Definition def = classdef.findName(sought, scope);

		if (def != null)
		{
			return def;
		}

		return (outer == null) ? null : outer.findName(sought, scope);
	}

	@Override
	public Definition findType(LexNameToken name)
	{
		Definition def = classdef.findType(name);

		if (def != null)
		{
			return def;
		}

		return (outer == null) ? null : outer.findType(name);
	}

	@Override
	public DefinitionSet findMatches(LexNameToken name)
	{
		DefinitionSet defs = classdef.findMatches(name);

		if (outer != null)
		{
			defs.addAll(outer.findMatches(name));
		}

		return defs;
	}

	@Override
	public void unusedCheck()
	{
		classdef.unusedCheck();
	}

	@Override
	public StateDefinition findStateDefinition()
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
		return (classdef instanceof SystemDefinition ||
				classdef instanceof CPUClassDefinition ||
				classdef instanceof BUSClassDefinition);
	}

	@Override
	public ClassDefinition findClassDefinition()
	{
		return classdef;
	}

	@Override
	public boolean isStatic()
	{
		return false;
	}
}
