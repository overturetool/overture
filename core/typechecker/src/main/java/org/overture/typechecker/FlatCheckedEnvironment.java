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

import org.overture.ast.definitions.PDefinition;
import org.overture.ast.typechecker.NameScope;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

/**
 * Define the type checking environment for a list of definitions, including a check for duplicates and name hiding.
 */

public class FlatCheckedEnvironment extends FlatEnvironment
{
	private boolean isStatic = false;

	public FlatCheckedEnvironment(ITypeCheckerAssistantFactory af,
			List<PDefinition> definitions, NameScope scope)
	{
		super(af, definitions);
		dupHideCheck(definitions, scope);
	}

	public FlatCheckedEnvironment(ITypeCheckerAssistantFactory af,
			List<PDefinition> definitions, Environment env, NameScope scope)
	{
		super(af, definitions, env);
		dupHideCheck(definitions, scope);
		setStatic(env.isStatic());
	}

	public FlatCheckedEnvironment(ITypeCheckerAssistantFactory af,
			PDefinition one, Environment env, NameScope scope)
	{
		super(af, one, env);
		dupHideCheck(definitions, scope);
		setStatic(env.isStatic());
	}

	// TODO: AccessSpecifier not defined
	// public void setStatic(AccessSpecifier access)
	// {
	// isStatic = access.isStatic;
	// }

	public void setStatic(boolean access)
	{
		isStatic = access;
	}

	@Override
	public boolean isStatic()
	{
		return isStatic;
	}
}
