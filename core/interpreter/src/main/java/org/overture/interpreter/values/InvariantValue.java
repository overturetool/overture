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

package org.overture.interpreter.values;

import java.util.Set;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.types.ANamedInvariantType;
import org.overture.ast.types.PType;
import org.overture.config.Settings;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.ContextException;
import org.overture.interpreter.runtime.ValueException;

public class InvariantValue extends ReferenceValue
{
	private static final long serialVersionUID = 1L;
	public final ANamedInvariantType type;
	private FunctionValue invariant;

	public InvariantValue(ANamedInvariantType type, Value value, Context ctxt)
			throws AnalysisException
	{
		super(value);
		this.type = type;

		invariant = ctxt.assistantFactory.createSInvariantTypeAssistant().getInvariant(type, ctxt);
		checkInvariant(ctxt);
	}

	public void checkInvariant(Context ctxt) throws AnalysisException
	{
		if (invariant != null && Settings.invchecks)
		{
			// In VDM++ and VDM-RT, we do not want to do thread swaps half way
			// through a DTC check (which can include calculating an invariant),
			// so we set the atomic flag around the conversion. This also stops
			// VDM-RT from performing "time step" calculations.

			boolean inv = false;

			try
			{
				ctxt.threadState.setAtomic(true);
				inv = invariant.eval(invariant.location, value, ctxt).boolValue(ctxt);
			} catch (ValueException e)
			{
				throw new ContextException(4060, e.getMessage(), invariant.location, ctxt);
			}

			finally
			{
				ctxt.threadState.setAtomic(false);
			}

			if (!inv)
			{
				abort(4060, "Type invariant violated for " + type.getName(), ctxt);
			}
		}
	}

	// For clone only
	private InvariantValue(ANamedInvariantType type, Value value,
			FunctionValue invariant)
	{
		super(value);
		this.type = type;
		this.invariant = invariant;
	}

	@Override
	protected Value convertValueTo(PType to, Context ctxt, Set<PType> done)
			throws AnalysisException
	{
		if (to.equals(type))
		{
			return this;
		} else
		{
			return value.convertValueTo(to, ctxt, done);
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
		UpdatableValue uval = UpdatableValue.factory(ival, listeners);

		if (invl != null)
		{
			// Update the listener with the address of the updatable copy
			invl.setValue(uval);
		}

		return uval;
	}

	@Override
	public Value getConstant()
	{
		return new InvariantValue(type, value.getConstant(), invariant);
	}

	@Override
	public Object clone()
	{
		return new InvariantValue(type, (Value) value.clone(), invariant);
	}
}
