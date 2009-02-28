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

import java.util.List;

import org.overturetool.vdmj.definitions.Definition;
import org.overturetool.vdmj.definitions.DefinitionList;


public class ModuleExports
{
	public final List<List<Export>> exports;

	public ModuleExports(List<List<Export>> exports)
	{
		this.exports = exports;
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();

		for (List<Export> type: exports)
		{
			for (Export sig: type)
			{
				sb.append(sig.toString());
				sb.append("\n");
			}
		}

		return sb.toString();
	}

	public DefinitionList getDefinitions()
	{
		DefinitionList exportDefs = new DefinitionList();

		for (List<Export> etype: exports)
		{
			for (Export exp: etype)
			{
				exportDefs.addAll(exp.getDefinition());
			}
		}

		return exportDefs;
	}

	public DefinitionList getDefinitions(DefinitionList actualDefs)
	{
		DefinitionList exportDefs = new DefinitionList();

		for (List<Export> etype: exports)
		{
			for (Export exp: etype)
			{
				exportDefs.addAll(exp.getDefinition(actualDefs));
			}
		}

		// Mark all exports as used

		for (Definition d: exportDefs)
		{
			d.markUsed();
		}

		return exportDefs;
	}
}
