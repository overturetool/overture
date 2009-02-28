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

package org.overturetool.vdmj.modules;

import org.overturetool.vdmj.definitions.DefinitionList;
import org.overturetool.vdmj.lex.LexIdentifierToken;
import org.overturetool.vdmj.lex.LexStringToken;

/**
 * A class to represent VDMTools-like dlmodules.
 */

public class DLModule extends Module
{
	/** The name of the uselib library. */
	public final LexStringToken library;

	public DLModule(LexIdentifierToken name,
		ModuleImports imports,
		ModuleExports exports,
		LexStringToken library)
	{
		super(name, imports, exports, new DefinitionList());
		this.library = library;
	}

	/**
	 * Process the dlmodule exports. This makes all the definitions in
	 * the dlmodule's exports section available as exports from the
	 * module.
	 */

	@Override
	public void processExports()
	{
		if (exports != null)
		{
			exportdefs.addAll(exports.getDefinitions());
		}
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("dlmodule " + name.name + "\n");

		if (imports != null)
		{
			sb.append("\nimports\n\n");
			sb.append(imports.toString());
		}

		if (exports != null)
		{
			sb.append("\nexports\n\n");
			sb.append(exports.toString());
		}
		else
		{
			sb.append("\nexports all\n\n");
		}

		if (library != null)
		{
			sb.append("uselib \"" + library.value + "\"\n\n");
		}

		sb.append("\nend " + name.name + "\n");

		return sb.toString();
	}
}
