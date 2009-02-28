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

package org.overturetool.vdmj.values;

import org.overturetool.vdmj.Settings;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.ValueException;
import org.overturetool.vdmj.types.NamedType;
import org.overturetool.vdmj.types.Type;

public class InvariantValue extends ReferenceValue
{
	public final NamedType type;
	public final FunctionValue invariant;

	public InvariantValue(NamedType type, Value value, Context ctxt)
		throws ValueException
	{
		super(value);
		this.type = type;
		this.invariant = type.getInvariant(ctxt);

		if (invariant != null && Settings.invchecks &&
			!invariant.eval(invariant.location, value, ctxt).boolValue(ctxt))
		{
			abort(4060, "Type invariant violated for " + type.typename, ctxt);
		}
	}

	// For clone only
	private InvariantValue(NamedType type, Value value, FunctionValue invariant)
	{
		super(value);
		this.type = type;
		this.invariant = invariant;
	}

	@Override
	public Value convertValueTo(Type to, Context ctxt) throws ValueException
	{
		if (to.isType(NamedType.class))
		{
			return this;
		}
		else
		{
			return value.convertValueTo(to, ctxt);
		}
	}

	@Override
	public Value getUpdatable(ValueListener listener)
	{
		return new UpdatableValue(
			new InvariantValue(
				type, value.getUpdatable(listener), invariant), listener);
	}

	@Override
	public Object clone()
	{
		return new InvariantValue(type, value, invariant);
	}
}
