/*******************************************************************************
 *
 *	Copyright (c) 2009 Fujitsu Services Ltd.
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

package org.overturetool.vdmj.traces;

import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.statements.Statement;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.UnknownType;
import org.overturetool.vdmj.values.ObjectValue;
import org.overturetool.vdmj.values.Value;
import org.overturetool.vdmj.values.VoidValue;

public class TraceVariableStatement extends Statement
{
	private static final long serialVersionUID = 1L;
	public final TraceVariable var;

	public TraceVariableStatement(TraceVariable var)
	{
		super(var.name.location);
		this.var = var;
	}

	@Override
	public Value eval(Context ctxt)
	{
		Value val = var.value;

		if (val.isType(ObjectValue.class))
		{
			val = (Value)var.value.clone();		// To allow updates to objects
		}

		ctxt.put(var.name, val);
		return new VoidValue();
	}

	@Override
	public String kind()
	{
		return "trace variable";
	}

	@Override
	public String toString()
	{
		return var.toString();
	}

	@Override
	public Type typeCheck(Environment env, NameScope scope)
	{
		return new UnknownType(location);
	}
}
