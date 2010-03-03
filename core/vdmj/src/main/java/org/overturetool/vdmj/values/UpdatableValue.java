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
import org.overturetool.vdmj.lex.Dialect;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.ValueException;
import org.overturetool.vdmj.types.Type;

/**
 * A class to hold an updatable value. This is almost identical to a
 * ReferenceValue, except that is has a set method which changes the
 * referenced value and calls a listener, and all the remaining methods
 * here are synchronized, to guarantee that the sets and gets see all
 * changes (to the same UpdateableValue) produced by other threads.
 */

public class UpdatableValue extends ReferenceValue
{
	private static final long serialVersionUID = 1L;
	public ValueListenerList listeners;

	public static UpdatableValue factory(Value value, ValueListenerList listeners)
	{
		if (Settings.dialect == Dialect.VDM_RT)
		{
			return new TransactionValue(value, listeners);
		}
		else
		{
			return new UpdatableValue(value, listeners);
		}
	}

	public static UpdatableValue factory(ValueListenerList listeners)
	{
		if (Settings.dialect == Dialect.VDM_RT)
		{
			return new TransactionValue(listeners);
		}
		else
		{
			return new UpdatableValue(listeners);
		}
	}

	protected UpdatableValue(Value value, ValueListenerList listeners)
	{
		super(value);
		this.listeners = listeners;
	}

	protected UpdatableValue(ValueListenerList listeners)
	{
		super();
		this.listeners = listeners;
	}

	@Override
	public synchronized Value getUpdatable(ValueListenerList watch)
	{
		return new UpdatableValue(value, watch);
	}

	@Override
	public synchronized Value convertValueTo(Type to, Context ctxt) throws ValueException
	{
		return value.convertValueTo(to, ctxt).getUpdatable(listeners);
	}

	@Override
	public void set(LexLocation location, Value newval, Context ctxt)
	{
		// Anything with structure added to an UpdateableValue has to be
		// updatable, otherwise you can "freeze" part of the substructure
		// such that it can't be changed.

		synchronized (this)
		{
    		if (newval instanceof UpdatableValue)
    		{
    			value = newval.deref();	// To avoid nested updatables
    		}
    		else
    		{
    			value = newval.getUpdatable(listeners).deref();
    		}
		}

		// The listeners are outside the sync because they have to lock
		// the object they notify, which can be holding a lock on this one.

		if (listeners != null)
		{
			listeners.changedValue(location, value, ctxt);
		}
	}

	public void addListener(ValueListener listener)
	{
		if (listeners != null)
		{
			listeners.add(listener);
		}
		else
		{
			listeners = new ValueListenerList(listener);
		}
	}

	@Override
	public synchronized Object clone()
	{
		return new UpdatableValue((Value)value.clone(), listeners);
	}

	@Override
	public synchronized boolean isType(Class<? extends Value> valueclass)
	{
		return valueclass.isInstance(this.value);
	}

	@Override
	public synchronized Value deref()
	{
		return value.deref();
	}

	@Override
	public synchronized boolean isUndefined()
	{
		return value.isUndefined();
	}

	@Override
	public synchronized boolean isVoid()
	{
		return value.isVoid();
	}

	@Override
	public synchronized double realValue(Context ctxt) throws ValueException
	{
		return value.realValue(ctxt);
	}

	@Override
	public synchronized long intValue(Context ctxt) throws ValueException
	{
		return value.intValue(ctxt);
	}

	@Override
	public synchronized long natValue(Context ctxt) throws ValueException
	{
		return value.nat1Value(ctxt);
	}

	@Override
	public synchronized long nat1Value(Context ctxt) throws ValueException
	{
		return value.nat1Value(ctxt);
	}

	@Override
	public synchronized boolean boolValue(Context ctxt) throws ValueException
	{
		return value.boolValue(ctxt);
	}

	@Override
	public synchronized char charValue(Context ctxt) throws ValueException
	{
		return value.charValue(ctxt);
	}

	@Override
	public synchronized ValueList tupleValue(Context ctxt) throws ValueException
	{
		return value.tupleValue(ctxt);
	}

	@Override
	public synchronized RecordValue recordValue(Context ctxt) throws ValueException
	{
		return value.recordValue(ctxt);
	}

	@Override
	public synchronized ObjectValue objectValue(Context ctxt) throws ValueException
	{
		return value.objectValue(ctxt);
	}

	@Override
	public synchronized String quoteValue(Context ctxt) throws ValueException
	{
		return value.quoteValue(ctxt);
	}

	@Override
	public synchronized ValueList seqValue(Context ctxt) throws ValueException
	{
		return value.seqValue(ctxt);
	}

	@Override
	public synchronized ValueSet setValue(Context ctxt) throws ValueException
	{
		return value.setValue(ctxt);
	}

	@Override
	public synchronized String stringValue(Context ctxt) throws ValueException
	{
		return value.stringValue(ctxt);
	}

	@Override
	public synchronized ValueMap mapValue(Context ctxt) throws ValueException
	{
		return value.mapValue(ctxt);
	}

	@Override
	public synchronized FunctionValue functionValue(Context ctxt) throws ValueException
	{
		return value.functionValue(ctxt);
	}

	@Override
	public synchronized OperationValue operationValue(Context ctxt) throws ValueException
	{
		return value.operationValue(ctxt);
	}

	@Override
	public synchronized boolean equals(Object other)
	{
		if (other instanceof Value)
		{
			Value val = ((Value)other).deref();

    		if (val instanceof ReferenceValue)
    		{
    			ReferenceValue rvo = (ReferenceValue)val;
    			return value.equals(rvo.value);
    		}
    		else
    		{
    			return value.equals(other);
    		}
		}

		return false;
	}

	@Override
	public synchronized String kind()
	{
		return value.kind();
	}

	@Override
	public synchronized int hashCode()
	{
		return value.hashCode();
	}

	@Override
	public synchronized String toString()
	{
		return value.toString();
	}
}
