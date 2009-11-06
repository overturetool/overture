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

import org.overturetool.vdmj.lex.LexNameList;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.values.NameValuePair;
import org.overturetool.vdmj.values.NameValuePairList;

/**
 * A class to hold a renamed import definition.
 */

public class RenamedDefinition extends Definition
{
	private static final long serialVersionUID = 1L;
	public final Definition def;

	public RenamedDefinition(LexNameToken name, Definition def, NameScope scope)
	{
		super(def.pass, name.location, name, scope);
		this.def = def;
		this.used = def.used;
	}

	@Override
	public Type getType()
	{
		return def.getType();
	}

	@Override
	public String toString()
	{
		return def + " renamed " + name;
	}

	@Override
	public void typeCheck(Environment base, NameScope scope)
	{
		def.typeCheck(base, scope);
	}

	@Override
	public DefinitionList getDefinitions()
	{
		return new DefinitionList(this);
	}

	@Override
	public LexNameList getVariableNames()
	{
		LexNameList both = new LexNameList(name);
		both.add(def.name);
		return both;
	}

	@Override
	public Definition findName(LexNameToken sought, NameScope scope)
	{
		Definition renamed = super.findName(sought, scope);

		if (renamed != null)
		{
			def.markUsed();
			return renamed;
		}
		else
		{
			return def.findName(sought, scope);
		}
	}

	@Override
	public Definition findType(LexNameToken sought)
	{
		Definition renamed = super.findName(sought, NameScope.TYPENAME);

		if (renamed != null && def instanceof TypeDefinition)
		{
			def.markUsed();
			return renamed;
		}
		else
		{
			return def.findType(sought);
		}
	}

	@Override
	public NameValuePairList getNamedValues(Context ctxt)
	{
		NameValuePairList renamed = new NameValuePairList();

		for (NameValuePair nv: def.getNamedValues(ctxt))
		{
			// We exclude any name from the definition other than the one
			// explicitly renamed. Otherwise, generated names like pre_f
			// come through and are not renamed.

			if (nv.name.equals(def.name))
			{
				renamed.add(new NameValuePair(name, nv.value));
			}
		}

		return renamed;
	}

	@Override
	public String kind()
	{
		return def.kind();
	}

	@Override
	public boolean isFunctionOrOperation()
	{
		return def.isFunctionOrOperation();
	}

	@Override
	public boolean isCallableOperation()
	{
		return def.isCallableOperation();
	}

	@Override
	public boolean isTypeDefinition()
	{
		return def.isTypeDefinition();
	}

	@Override
	public boolean isValueDefinition()
	{
		return def.isValueDefinition();
	}

	@Override
	public boolean isRuntime()
	{
		return def.isRuntime();
	}
}
