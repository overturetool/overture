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

import org.overturetool.vdmj.definitions.AccessSpecifier;
import org.overturetool.vdmj.definitions.Definition;
import org.overturetool.vdmj.definitions.DefinitionList;

/**
 * Define the type checking environment for a list of definitions, including
 * a check for duplicates and name hiding.
 */

public class FlatCheckedEnvironment extends FlatEnvironment
{
	private boolean isStatic = false;

	public FlatCheckedEnvironment(
		DefinitionList definitions, NameScope scope)
	{
		super(definitions);
		dupHideCheck(definitions, scope);
	}

	public FlatCheckedEnvironment(
		DefinitionList definitions, Environment env, NameScope scope)
	{
		super(definitions, env);
		dupHideCheck(definitions, scope);
		setStatic(env.isStatic());
	}

	public FlatCheckedEnvironment(
		Definition one, Environment env, NameScope scope)
	{
		super(one, env);
		dupHideCheck(definitions, scope);
		setStatic(env.isStatic());
	}

	public void setStatic(AccessSpecifier access)
	{
		isStatic = access.isStatic;
	}

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
