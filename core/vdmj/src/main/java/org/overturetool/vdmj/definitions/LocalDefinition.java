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

import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.lex.LexNameList;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.typechecker.Pass;
import org.overturetool.vdmj.types.ParameterType;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.UnknownType;
import org.overturetool.vdmj.values.NameValuePair;
import org.overturetool.vdmj.values.NameValuePairList;

/**
 * A class to hold a local variable definition.
 */

public class LocalDefinition extends Definition
{
	private static final long serialVersionUID = 1L;
	public Type type;
	private boolean valueDefinition = false;

	public LocalDefinition(LexLocation location,
		LexNameToken name, NameScope scope, Type type)
	{
		super(Pass.DEFS, location, name, scope);
		this.type = type;
	}

	@Override
	public String toString()
	{
		return name.name + " = " + type;
	}

	@Override
	public void typeCheck(Environment base, NameScope scope)
	{
   		if (type != null)
   		{
   			type = type.typeResolve(base, null);
   		}
	}

	@Override
	public Type getType()
	{
		return type == null ? new UnknownType(location) : type;
	}

	@Override
	public DefinitionList getDefinitions()
	{
		return new DefinitionList(this);
	}

	@Override
	public LexNameList getVariableNames()
	{
		return new LexNameList(name);
	}

	@Override
	public NameValuePairList getNamedValues(Context ctxt)
	{
		NameValuePair nvp = new NameValuePair(name, ctxt.lookup(name));
		return new NameValuePairList(nvp);
	}

	@Override
	public String kind()
	{
		return "local";
	}

	@Override
	public boolean isFunction()
	{
		// This is only true for local definitions of member functions or
		// operations, not local definitions that happen to be function values.
		// So we exclude parameter types.

		return type.isType(ParameterType.class) ? false : type.isFunction();
	}

	@Override
	public boolean isUpdatable()
	{
		return nameScope.matches(NameScope.STATE);
	}

	public void setValueDefinition()
	{
		valueDefinition  = true;
	}

	@Override
	public boolean isValueDefinition()
	{
		return valueDefinition;
	}
}
