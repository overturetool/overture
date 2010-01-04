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

import org.overturetool.vdmj.definitions.Definition;
import org.overturetool.vdmj.definitions.DefinitionList;
import org.overturetool.vdmj.definitions.LocalDefinition;
import org.overturetool.vdmj.lex.LexNameList;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.FlatCheckedEnvironment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.types.ParameterType;
import org.overturetool.vdmj.types.Type;

public class ImportedFunction extends ImportedValue
{
	private static final long serialVersionUID = 1L;

	public final LexNameList typeParams;

	public ImportedFunction(
		LexNameToken name, Type type, LexNameList typeParams, LexNameToken renamed)
	{
		super(name, type, renamed);
		this.typeParams = typeParams;
	}

	@Override
	public void typeCheck(Environment env)
	{
		if (typeParams == null)
		{
			super.typeCheck(env);
		}
		else
		{
    		DefinitionList defs = new DefinitionList();

    		for (LexNameToken pname: typeParams)
    		{
    			Definition p = new LocalDefinition(
    				pname.location, pname, NameScope.NAMES, new ParameterType(pname));

    			p.markUsed();
    			defs.add(p);
    		}

    		FlatCheckedEnvironment params =	new FlatCheckedEnvironment(
    			defs, env, NameScope.NAMES);

    		super.typeCheck(params);
		}
	}

	@Override
	public String toString()
	{
		return "import function " + name +
				(typeParams == null ? "" : "[" + typeParams + "]") +
				(renamed == null ? "" : " renamed " + renamed.name) +
				(type == null ? "" : ":" + type);
	}
}
