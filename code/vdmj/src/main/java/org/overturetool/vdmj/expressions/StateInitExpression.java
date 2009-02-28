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

package org.overturetool.vdmj.expressions;

import org.overturetool.vdmj.definitions.StateDefinition;
import org.overturetool.vdmj.patterns.IdentifierPattern;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.ValueException;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.types.BooleanType;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.TypeList;
import org.overturetool.vdmj.values.BooleanValue;
import org.overturetool.vdmj.values.FunctionValue;
import org.overturetool.vdmj.values.RecordValue;
import org.overturetool.vdmj.values.Value;

public class StateInitExpression extends Expression
{
	public final StateDefinition state;

	public StateInitExpression(StateDefinition state)
	{
		super(state.location);
		this.state = state;
		location.executable(false);
	}

	@Override
	public Value eval(Context ctxt)
	{
		breakpoint.check(location, ctxt);

		try
		{
			FunctionValue invariant = state.invfunc;

			// Note, the function just checks whether the argument passed would
			// violate the state invariant (if any). It doesn't initialize the
			// state itself. This is done in State.initialize().

			if (invariant != null)
			{
				IdentifierPattern argp = (IdentifierPattern)state.initPattern;
				RecordValue rv = (RecordValue)ctxt.lookup(argp.name);
				return invariant.eval(location, rv, ctxt);
			}

			return new BooleanValue(true);
		}
		catch (ValueException e)
		{
			return abort(e);
		}
	}

	@Override
	public String toString()
	{
		return state.name.name;
	}

	@Override
	public Type typeCheck(Environment env, TypeList qualifiers, NameScope scope)
	{
		return new BooleanType(location);
	}

	@Override
	public String kind()
	{
		return "state init";
	}
}
