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

import org.overturetool.vdmj.definitions.Definition;
import org.overturetool.vdmj.definitions.TypeDefinition;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.ValueException;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.values.ParameterValue;
import org.overturetool.vdmj.values.Value;
import org.overturetool.vdmj.values.ValueList;


public class ParameterType extends Type
{
	private static final long serialVersionUID = 1L;
	public final LexNameToken name;

	public ParameterType(LexNameToken name)
	{
		super(name.location);
		this.name = name;
	}

	@Override
	public Type typeResolve(Environment env, TypeDefinition root)
	{
		if (resolved) return this; else resolved = true;

		Definition p = env.findName(name, NameScope.NAMES);

		if (p == null || !(p.getType() instanceof ParameterType))
		{
			report(3433, "Parameter type @" + name + " not defined");
		}

		return this;
	}
	
	@Override
	public ValueList getAllValues(Context ctxt) throws ValueException
	{
		Value t = ctxt.lookup(name);

		if (t == null)
		{
			abort(4008, "No such type parameter @" + name + " in scope", ctxt);
		}
		else if (t instanceof ParameterValue)
		{
			ParameterValue tv = (ParameterValue)t;
			return tv.type.getAllValues(ctxt);
		}
		
		abort(4009, "Type parameter/local variable name clash, @" + name, ctxt);
		return null;
	}

	@Override
	public Type polymorph(LexNameToken pname, Type actualType)
	{
		return (name.equals(pname)) ? actualType : this;
	}

	@Override
	public int hashCode()
	{
		return name.hashCode();
	}

	@Override
	public String toDisplay()
	{
		return "@" + name;
	}
}
