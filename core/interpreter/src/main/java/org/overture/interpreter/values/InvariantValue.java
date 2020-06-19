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
import org.overture.ast.types.AOptionalType;
import org.overture.ast.types.PType;
import org.overture.config.Settings;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.ContextException;
import org.overture.interpreter.runtime.Interpreter;
import org.overture.interpreter.runtime.ValueException;

public class InvariantValue extends ReferenceValue
{
	private static final long serialVersionUID = 1L;
	public final ANamedInvariantType type;
	private final FunctionValue invariant;
	private final FunctionValue equality;
	private final FunctionValue ordering;

	public InvariantValue(ANamedInvariantType type, Value value, Context ctxt)
			throws AnalysisException
	{
		super(value);
		this.type = type;

		invariant = ctxt.assistantFactory.createSInvariantTypeAssistant().getInvariant(type, ctxt);
		equality = ctxt.assistantFactory.createSInvariantTypeAssistant().getEquality(type, ctxt);
		ordering = ctxt.assistantFactory.createSInvariantTypeAssistant().getOrder(type, ctxt);
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
						   FunctionValue invariant, FunctionValue equality, FunctionValue ordering)
	{
		super(value);
		this.type = type;
		this.invariant = invariant;
		this.equality = equality;
		this.ordering = ordering;
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

		InvariantValue ival = new InvariantValue(type, value.getUpdatable(listeners), invariant, equality, ordering);
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
		return new InvariantValue(type, value.getConstant(), invariant, equality, ordering);
	}

	@Override
	public Object clone()
	{
		return new InvariantValue(type, (Value) value.clone(), invariant, equality, ordering);
	}

	@Override
	public int compareTo(Value other)
	{
		if (ordering != null &&
			other instanceof InvariantValue &&
			((InvariantValue)other).type.equals(type))
		{
			Context ctxt = Interpreter.getInstance().initialContext;
			ctxt.setThreadState(null, null);
			ctxt.threadState.setAtomic(true);

			try
			{
				ValueList args = new ValueList();
				args.add(this);
				args.add(other);

				if (ordering.eval(ordering.location, args, ctxt).boolValue(ctxt))
				{
					return -1;	// Less
				}
				else if (equals(other))
				{
					return 0;	// Equal
				}
				else
				{
					return 1;	// More
				}
			}
			catch (ValueException e)
			{
				throw new RuntimeException(e);
			}
			catch (AnalysisException e)
			{
				throw new RuntimeException(e);
			}
			finally
			{
				ctxt.threadState.setAtomic(false);
			}
		}
		else if (equality != null && equals(other))
		{
			// Works with Maps of invariants that define "eq" (ValueMap is a TreeMap)
			return 0;
		}
		else
		{
			return super.compareTo(other);
		}
	}

	@Override
	public boolean equals(Object other)
	{
		if (other instanceof Value)
		{
			if (equality != null)
			{
    			if (other instanceof NilValue && !(type.getType() instanceof AOptionalType))
    			{
    				return false;
    			}

    			Context ctxt = Interpreter.getInstance().initialContext;
				ctxt.setThreadState(null, null);
				ctxt.threadState.setAtomic(true);

				try
				{
					ValueList args = new ValueList();
					args.add(this);
					args.add((Value)other);
					return equality.eval(equality.location, args, ctxt).boolValue(ctxt);
				}
				catch (ValueException e)
				{
					throw new RuntimeException(e);
				}
				catch (AnalysisException e)
				{
					throw new RuntimeException(e);
				}
				finally
				{
					ctxt.threadState.setAtomic(false);
				}
			}
			else
			{
				return super.equals(other);
			}
		}

		return false;
	}

	@Override
	public boolean isOrdered()
	{
		return (ordering != null) ? true : value.isOrdered();
	}
}
