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

package org.overturetool.vdmj.modules;

import java.io.Serializable;
import java.util.List;

import org.overturetool.vdmj.definitions.DefinitionList;
import org.overturetool.vdmj.lex.LexIdentifierToken;
import org.overturetool.vdmj.typechecker.Environment;


public class ImportFromModule implements Serializable
{
	private static final long serialVersionUID = 1L;

	public final LexIdentifierToken name;
	public final List<List<Import>> signatures;

	public ImportFromModule(
		LexIdentifierToken name, List<List<Import>> signatures)
	{
		this.name = name;
		this.signatures = signatures;
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("from " + name + "\n");

		for (List<Import> type: signatures)
		{
			if (type instanceof ImportAll)
			{
				sb.append("import all\n");
			}
			else for (Import sig: type)
			{
				sb.append(sig.toString());
				sb.append("\n");
			}
		}

		return sb.toString();
	}

	public DefinitionList getDefinitions(Module from)
	{
		DefinitionList defs = new DefinitionList();

		for (List<Import> ofType: signatures)
		{
			for (Import imp: ofType)
			{
				defs.addAll(imp.getDefinitions(from));
			}
		}

		return defs;
	}

	public void typeCheck(Environment env)
	{
		for (List<Import> ofType: signatures)
		{
			for (Import imp: ofType)
			{
				imp.typeCheck(env);
			}
		}
	}
}
