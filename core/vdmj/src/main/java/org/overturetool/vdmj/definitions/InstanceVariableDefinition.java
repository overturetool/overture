/*******************************************************************************
 *
 *	Copyright (C) 2008 Fujitsu Services Ltd.
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

package org.overturetool.vdmj.definitions;

import org.overturetool.vdmj.expressions.Expression;
import org.overturetool.vdmj.expressions.UndefinedExpression;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.typechecker.PrivateClassEnvironment;
import org.overturetool.vdmj.typechecker.TypeCheckException;
import org.overturetool.vdmj.types.Type;

/**
 * A class to represent instance variable definitions.
 */

public class InstanceVariableDefinition extends AssignmentDefinition
{
	private static final long serialVersionUID = 1L;
	public final LexNameToken oldname;

	public InstanceVariableDefinition(
		LexNameToken name, Type type, Expression expression)
	{
		super(name, type, expression);
		oldname = name.getOldName();
	}

	@Override
	public boolean isInstanceVariable()
	{
		return true;
	}

	@Override
	public void typeResolve(Environment env)
	{
		try
		{
			type = type.typeResolve(env, null);
		}
		catch (TypeCheckException e)
		{
			type.unResolve();
			throw e;
		}
	}

	@Override
	public void typeCheck(Environment base, NameScope scope)
	{
		if (expression instanceof UndefinedExpression)
		{
			if (accessSpecifier.isStatic)
			{
				report(3037, "Static instance variable is not initialized: " + name);
			}
			else
			{
				warning(5001, "Instance variable is not initialized: " + name);
			}
		}

		// Initializers can reference class members, so create a new env.
		// We set the type qualifier to unknown so that type-based name
		// resolution will succeed.

		Environment cenv = new PrivateClassEnvironment(classDefinition, base);
		super.typeCheck(cenv, NameScope.NAMESANDSTATE);
	}

	@Override
	public Definition findName(LexNameToken sought, NameScope scope)
	{
		Definition found = super.findName(sought, scope);
		if (found != null) return found;
		return scope.matches(NameScope.OLDSTATE) &&
				oldname.equals(sought) ? this : null;
	}

	@Override
	public String kind()
	{
		return "instance variable";
	}
}
