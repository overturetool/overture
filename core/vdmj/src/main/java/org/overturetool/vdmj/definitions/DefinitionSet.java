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

package org.overturetool.vdmj.definitions;

import java.util.HashSet;

/**
 * A class to hold a set of Definitions with unique names.
 */

@SuppressWarnings("serial")
public class DefinitionSet extends HashSet<Definition>
{
	public DefinitionSet()
	{
		super();
	}

	public DefinitionSet(Definition definition)
	{
		add(definition);
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();

		for (Definition d: this)
		{
			sb.append(d.accessSpecifier.toString());
			sb.append(" ");

			sb.append(d.kind() + " " + d.getVariableNames() + ":" + d.getType());
			sb.append("\n");
		}

		return sb.toString();
	}

	public DefinitionList asList()
	{
		DefinitionList list = new DefinitionList();
		list.addAll(this);
		return list;
	}
}
