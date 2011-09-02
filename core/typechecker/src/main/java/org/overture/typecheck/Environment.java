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

import java.util.List;
import java.util.Set;


import org.overture.ast.definitions.AStateDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.definitions.assistants.PDefinitionAssistantTC;
import org.overture.ast.definitions.assistants.PDefinitionListAssistant;
import org.overturetool.vdmj.lex.LexNameList;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.typechecker.NameScope;



/**
 * The parent class of all type checking environments.
 */

abstract public class Environment
{
	/** The environment chain. */
	protected final Environment outer;

	/** The enclosing func/op definition at this point, or null. */
	private PDefinition enclosingDefinition = null;

	/**
	 * Create an environment linking to the given outer chain.
	 * @param outer
	 */

	public Environment(Environment outer)
	{
		this.outer = outer;
	}

	/**
	 * Check whether the list of definitions passed contains any duplicates,
	 * or whether any names in the list hide the same name further down the
	 * environment chain.
	 *
	 * @param list	The list of definitions to check.
	 */

	protected void dupHideCheck(List<PDefinition> list, NameScope scope)
	{
		LexNameList allnames = PDefinitionListAssistant.getVariableNames(list);

		for (LexNameToken n1: allnames)
		{
			LexNameList done = new LexNameList();

			for (LexNameToken n2: allnames)
			{
				if (n1 != n2 && n1.equals(n2) && !done.contains(n1))
				{
					TypeChecker.warning(5007, "Duplicate definition: " + n1, n1.getLocation());
					done.add(n1);
				}
			}

			if (outer != null)
			{
				// We search for any scoped name (ie. the first), but then check
				// the scope matches what we can see. If we pass scope to findName
				// it throws errors if the name does not match the scope.

				PDefinition def = outer.findName(n1, NameScope.NAMESANDSTATE);

				if (def != null && def.getLocation() != n1.getLocation() &&
					def.getNameScope().matches(scope))
				{
					// Reduce clutter for names in the same module/class
					String message = null;

					if (def.getLocation().file.equals(n1.getLocation().file))
					{
						message = def.getName() + " " + def.getLocation().toShortString() +
							" hidden by " +	n1.getName();
					}
					else
					{
						message = def.getName() + " " + def.getLocation() +
							" hidden by " + n1.getName();
					}

					TypeChecker.warning(5008, message, n1.getLocation());
				}
			}
		}
	}

	public PDefinition getEnclosingDefinition()
	{
		if (enclosingDefinition != null)
		{
			return enclosingDefinition;
		}

		return outer == null ? null : outer.getEnclosingDefinition();
	}

	public void setEnclosingDefinition(PDefinition def)
	{
		enclosingDefinition = def;
	}

	/** Find a name in the environment of the given scope. */
	abstract public PDefinition findName(LexNameToken name, NameScope scope);

	/** Find a type in the environment. */
	abstract public PDefinition findType(LexNameToken name, String fromModule);

	/** Find the state defined in the environment, if any. */
	abstract public AStateDefinition findStateDefinition();

	/** Find the enclosing class definition, if any. */
	abstract public SClassDefinition findClassDefinition();

	/** True if the calling context is a static function or operation. */
	abstract public boolean isStatic();

	/** Check whether any definitions in the environment were unused. */
	abstract public void unusedCheck();

	/** True if this is a VDM++ environment. */
	abstract public boolean isVDMPP();

	/** True if this is a VDM-RT "system" environment. */
	abstract public boolean isSystem();

	/** Find functions and operations of the given basic name. */
	abstract public Set<PDefinition> findMatches(LexNameToken name);

	/** Mark all definitions, at this level, used. */
	public void markUsed()
	{
		// Nothing, by default. Implemented in flat environments.
	}

	/** Add details to a TC error with alternative fn/op name possibilities. */
	public void listAlternatives(LexNameToken name)
	{
		for (PDefinition possible: findMatches(name))
		{
			if (PDefinitionAssistantTC.isFunctionOrOperation(possible))
			{
				TypeChecker.detail("Possible", possible.getName());
			}
		}
	}

	/** Unravelling unused check. */
	public void unusedCheck(Environment downTo)
	{
		Environment p = this;

		while (p != null && p != downTo)
		{
			p.unusedCheck();
			p = p.outer;
		}
	}
}
