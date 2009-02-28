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

import java.util.List;

import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.lex.LexNameList;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.patterns.MultipleBind;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.typechecker.Pass;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.UnknownType;
import org.overturetool.vdmj.util.Utils;


/**
 * A class to hold a multiple bind list definition.
 */

public class MultiBindListDefinition extends Definition
{
	public final List<MultipleBind> bindings;

	private DefinitionList defs = null;

	public MultiBindListDefinition(LexLocation location, List<MultipleBind> bindings)
	{
		super(Pass.DEFS, location, null, null);
		this.bindings = bindings;
	}

	@Override
	public String toString()
	{
		return "def " + Utils.listToString(bindings);
	}

	@Override
	public void typeCheck(Environment base, NameScope scope)
	{
		defs = new DefinitionList();

		for (MultipleBind mb: bindings)
		{
			Type type = mb.typeCheck(base, scope);
			defs.addAll(mb.getDefinitions(type, scope));
		}

		defs.typeCheck(base, scope);
	}

	@Override
	public Definition findName(LexNameToken sought, NameScope incState)
	{
		if (defs != null)
		{
			Definition def = defs.findName(sought, incState);

			if (def != null)
			{
				return def;
			}
		}

		return null;
	}

	@Override
	public Type getType()
	{
		if (defs != null && defs.size() == 1)
		{
			return defs.get(0).getType();
		}

		return new UnknownType(location);
	}

	@Override
	public void unusedCheck()
	{
		if (defs != null)
		{
			defs.unusedCheck();
		}
	}

	@Override
	public DefinitionList getDefinitions()
	{
		return defs == null ? new DefinitionList() : defs;
	}

	@Override
	public LexNameList getVariableNames()
	{
		return defs == null ? new LexNameList() : defs.getVariableNames();
	}

	@Override
	public String kind()
	{
		return "bind";
	}
}
