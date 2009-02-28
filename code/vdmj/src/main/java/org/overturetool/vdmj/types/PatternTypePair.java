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

package org.overturetool.vdmj.types;

import org.overturetool.vdmj.definitions.DefinitionList;
import org.overturetool.vdmj.patterns.Pattern;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.NameScope;

public class PatternTypePair
{
	public final Pattern pattern;
	public Type type;
	private boolean resolved = false;

	public PatternTypePair(Pattern pattern, Type type)
	{
		this.pattern = pattern;
		this.type = type;
	}

	public void typeResolve(Environment base)
	{
		if (resolved ) return; else { resolved = true; }
		type = type.typeResolve(base, null);
	}

	public DefinitionList getDefinitions()
	{
		return pattern.getDefinitions(type, NameScope.LOCAL);
	}

	@Override
	public String toString()
	{
		return pattern + ":" + type;
	}
}
