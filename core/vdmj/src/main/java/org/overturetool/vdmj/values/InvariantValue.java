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
	private static final long serialVersionUID = 1L;
	public final NamedType type;
	private FunctionValue invariant;

	public InvariantValue(NamedType type, Value value, Context ctxt)
		throws ValueException
	{
		super(value);
		this.type = type;

		invariant = type.getInvariant(ctxt);
		checkInvariant(ctxt);
	}
	
	public void checkInvariant(Context ctxt) throws ValueException
	{
		if (invariant != null && Settings.invchecks)
		{
			// In VDM++ and VDM-RT, we do not want to do thread swaps half way
			// through a DTC check (which can include calculating an invariant),
			// so we set the atomic flag around the conversion. This also stops
			// VDM-RT from performing "time step" calculations.
			
			ctxt.threadState.setAtomic(true);
			boolean inv = invariant.eval(invariant.location, value, ctxt).boolValue(ctxt);
			ctxt.threadState.setAtomic(false);

			if (!inv)
			{
				abort(4060, "Type invariant violated for " + type.typename, ctxt);
			}
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
		if (to.equals(type))
		{
			return this;
		}
		else
		{
			return value.convertValueTo(to, ctxt);
		}
	}

	@Override
	public Value getUpdatable(ValueListenerList listeners)
	{
		InvariantValueListener invl = null;
		
		if (invariant != null)
		{
			// Add an invariant listener to a new list for children of this value
			// We update the object in the listener once we've created it (below)
			
			invl = new InvariantValueListener();
			ValueListenerList list = new ValueListenerList(invl);
			
			if (listeners != null)
			{
				list.addAll(listeners);
			}
			
			listeners = list;
		}
		
		InvariantValue ival = new InvariantValue(type, value.getUpdatable(listeners), invariant);
		Value uval = UpdatableValue.factory(ival, listeners);
		
		if (invl != null)
		{
			// Update the listener with the address of the updatable copy
			invl.setValue(ival);
		}
		
		return uval;
	}

	@Override
	public Object clone()
	{
		return new InvariantValue(type, value, invariant);
	}
}
