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

import java.io.Serializable;

import org.overturetool.vdmj.definitions.DefinitionList;
import org.overturetool.vdmj.patterns.Pattern;
import org.overturetool.vdmj.patterns.PatternList;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.NameScope;

public class PatternListTypePair implements Serializable
{
	private static final long serialVersionUID = 1L;
	public final PatternList patterns;
	public Type type;

	public PatternListTypePair(PatternList patterns, Type type)
	{
		this.patterns = patterns;
		this.type = type;
	}

	public TypeList getTypeList()
	{
		TypeList list = new TypeList();

		for (int i=0; i<patterns.size(); i++)
		{
			list.add(type);
		}

		return list;
	}

	public DefinitionList getDefinitions(NameScope scope)
	{
		DefinitionList list = new DefinitionList();

		for (Pattern p: patterns)
		{
			list.addAll(p.getDefinitions(type, scope));
		}

		return list;
	}

	public void typeResolve(Environment base)
	{
		patterns.typeResolve(base);
	}

	@Override
	public String toString()
	{
		return "(" + patterns + ":" + type + ")";
	}
}
