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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.overture.ast.definitions.AStateDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.typechecker.NameScope;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

/**
 * Define the type checking environment for a list of definitions.
 */

public class FlatEnvironment extends Environment
{
	protected final List<PDefinition> definitions;
	private boolean limitStateScope = false;

	public List<PDefinition> getDefinitions()
	{
		return definitions;
	}

	public FlatEnvironment(ITypeCheckerAssistantFactory af,
			List<PDefinition> definitions)
	{
		super(af, null);
		this.definitions = definitions;
	}

	public FlatEnvironment(ITypeCheckerAssistantFactory af,
			List<PDefinition> definitions, Environment env)
	{
		super(af, env);
		this.definitions = definitions;
	}

	public FlatEnvironment(ITypeCheckerAssistantFactory af, PDefinition one,
			Environment env)
	{
		super(af, env);
		this.definitions = new ArrayList<PDefinition>();
		this.definitions.add(one);
	}

	public FlatEnvironment(ITypeCheckerAssistantFactory af, Environment env, boolean functional)
	{
		this(af, new ArrayList<PDefinition>(), env);
		setFunctional(functional);
	}

	public void add(PDefinition one)
	{
		definitions.add(one);
	}

	@Override
	public PDefinition findName(ILexNameToken name, NameScope scope)
	{
		PDefinition def = af.createPDefinitionListAssistant().findName(definitions, name, scope);

		if (def != null && !ExcludedDefinitions.isExcluded(def))
		{
			return def;
		}

		if (outer == null)
		{
			return null;
		} else
		{
			if (limitStateScope)
			{
				scope = NameScope.NAMES; // Limit NAMESAND(ANY)STATE
			}

			return outer.findName(name, scope);
		}
	}

	@Override
	public PDefinition findType(ILexNameToken name, String fromModule)
	{
		PDefinition def = af.createPDefinitionAssistant().findType(definitions, name, fromModule);

		if (def != null)
		{
			return def;
		}

		return outer == null ? null : outer.findType(name, fromModule);
	}

	@Override
	public AStateDefinition findStateDefinition()
	{
		AStateDefinition def = af.createPDefinitionListAssistant().findStateDefinition(definitions);

		if (def != null)
		{
			return def;
		}

		return outer == null ? null : outer.findStateDefinition();
	}

	@Override
	public void unusedCheck()
	{
		af.createPDefinitionListAssistant().unusedCheck(definitions);
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
	public SClassDefinition findClassDefinition()
	{
		return outer == null ? null : outer.findClassDefinition();
	}

	@Override
	public boolean isStatic()
	{
		return outer == null ? false : outer.isStatic();
	}

	@Override
	public Set<PDefinition> findMatches(ILexNameToken name)
	{
		Set<PDefinition> defs = af.createPDefinitionListAssistant().findMatches(definitions, name);

		if (outer != null)
		{
			defs.addAll(outer.findMatches(name));
		}

		return defs;
	}

	@Override
	public void markUsed()
	{
		af.createPDefinitionListAssistant().markUsed(definitions);
	}

	public void setLimitStateScope(boolean limitStateScope)
	{
		this.limitStateScope = limitStateScope;
	}

}
