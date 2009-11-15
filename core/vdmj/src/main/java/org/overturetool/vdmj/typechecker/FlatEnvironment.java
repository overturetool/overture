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
import org.overturetool.vdmj.definitions.DefinitionList;
import org.overturetool.vdmj.definitions.DefinitionSet;
import org.overturetool.vdmj.definitions.StateDefinition;
import org.overturetool.vdmj.lex.LexNameToken;

/**
 * Define the type checking environment for a list of definitions.
 */

public class FlatEnvironment extends Environment
{
	protected final DefinitionList definitions;

	public FlatEnvironment(DefinitionList definitions)
	{
		super(null);
		this.definitions = definitions;
	}

	public FlatEnvironment(DefinitionList definitions, Environment env)
	{
		super(env);
		this.definitions = definitions;
	}

	public FlatEnvironment(Definition one, Environment env)
	{
		super(env);
		this.definitions = new DefinitionList(one);
	}

	public void add(Definition one)
	{
		definitions.add(one);
	}

	@Override
	public Definition findName(LexNameToken name, NameScope scope)
	{
		Definition def = definitions.findName(name, scope);

		if (def != null)
		{
			return def;
		}

		return (outer == null) ? null : outer.findName(name, scope);
	}

	@Override
	public Definition findType(LexNameToken name)
	{
		Definition def = definitions.findType(name);

		if (def != null)
		{
			return def;
		}

		return (outer == null) ? null : outer.findType(name);
	}

	@Override
	public StateDefinition findStateDefinition()
	{
		StateDefinition def = definitions.findStateDefinition();

		if (def != null)
		{
			return def;
		}

   		return (outer == null) ? null : outer.findStateDefinition();
	}

	@Override
	public void unusedCheck()
	{
		definitions.unusedCheck();
	}

	@Override
	public boolean isVDMPP()
	{
		return outer == null ? false : outer.isVDMPP();
	}

	@Override
	public boolean isSystem()
	{
		return outer == null ? false : outer.isSystem();
	}

	@Override
	public ClassDefinition findClassDefinition()
	{
		return outer == null ? null : outer.findClassDefinition();
	}

	@Override
	public boolean isStatic()
	{
		return outer == null ? false : outer.isStatic();
	}

	@Override
	public DefinitionSet findMatches(LexNameToken name)
	{
		DefinitionSet defs = definitions.findMatches(name);

		if (outer != null)
		{
			defs.addAll(outer.findMatches(name));
		}

		return defs;
	}

	@Override
    public void markUsed()
    {
		definitions.markUsed();
    }
}
