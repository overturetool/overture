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

package org.overturetool.vdmj.definitions;

import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.lex.LexNameList;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.typechecker.Pass;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.UnknownType;
import org.overturetool.vdmj.values.NameValuePairList;

/**
 * A class to hold a definition of, as yet, an unknown type. See ValueDefinition
 * and ExportedValue for the purpose of this class.
 */

public class UntypedDefinition extends Definition
{
	public UntypedDefinition(
		LexLocation location, LexNameToken name, NameScope scope)
	{
		super(Pass.DEFS, location, name, scope);
	}

	@Override
	public Type getType()
	{
		return new UnknownType(location);
	}

	@Override
	public String toString()
	{
		return "Unknown type definition " + name;
	}

	@Override
	public void typeCheck(Environment base, NameScope scope)
	{
		assert false: "Can't type check untyped definition?";
	}

	@Override
	public DefinitionList getDefinitions()
	{
		return new DefinitionList(this);
	}

	@Override
	public LexNameList getVariableNames()
	{
		assert false: "Can't get variables of untyped definition?";
		return null;
	}

	@Override
	public NameValuePairList getNamedValues(Context ctxt)
	{
		assert false: "Can't get name/values of untyped definition?";
		return null;
	}

	@Override
	public String kind()
	{
		return "untyped";
	}
}
