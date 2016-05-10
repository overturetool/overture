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

import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.Vector;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.lex.Dialect;
import org.overture.ast.types.PType;
import org.overture.config.Settings;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.ContextException;
import org.overture.interpreter.runtime.ValueException;
import org.overture.interpreter.scheduler.BasicSchedulableThread;
import org.overture.interpreter.scheduler.SharedStateListner;

/**
 * A class to hold an updatable value that can be modified by VDM-RT threads in transactions, committed at a duration
 * point.
 */

public class TransactionValue extends UpdatableValue
{
	private static final long serialVersionUID = 1L;

	private static List<TransactionValue> commitList = new Vector<>();

	private Value newvalue = null; // The pending value before a commit
	private long newthreadid = -1; // The thread that made the change

	private ILexLocation lastSetLocation = null; // The location that made the change

	protected TransactionValue(Value value, ValueListenerList listeners,
			PType type)
	{
		super(value, listeners, type);
		newvalue = value;
	}

	protected TransactionValue(ValueListenerList listeners, PType type)
	{
		super(listeners, type);
		newvalue = value;
	}

	private Value select()
	{
		if (newthreadid > 0
				&& BasicSchedulableThread.getThread(Thread.currentThread()) != null
				&& BasicSchedulableThread.getThread(Thread.currentThread()).getId() == newthreadid)
		{
			return newvalue;
		} else
		{
			return value;
		}
	}

	@Override
	public synchronized Value getUpdatable(ValueListenerList watch)
	{
		return new TransactionValue(select(), watch, restrictedTo);
	}

	@Override
	protected
	synchronized Value convertValueTo(PType to, Context ctxt, Set<PType> done)
			throws AnalysisException
	{
		return select().convertValueTo(to, ctxt, done).getUpdatable(listeners);
	}

	@Override
	public void set(ILexLocation location, Value newval, Context ctxt)
			throws AnalysisException
	{
		long current = BasicSchedulableThread.getThread(Thread.currentThread()).getId();

		if (newthreadid > 0 && current != newthreadid)
		{
			throw new ContextException(4142, "Value already updated by thread "
					+ newthreadid, location, ctxt);
		}

		synchronized (this)
		{
			lastSetLocation = location;
			newvalue = newval.getUpdatable(listeners);
			newvalue = ((UpdatableValue) newvalue).value; // To avoid nested updatables

			if (restrictedTo != null)
			{
				newvalue = newvalue.convertTo(restrictedTo, ctxt);
			}
		}

		if (newthreadid < 0)
		{
			synchronized (commitList)
			{
				newthreadid = BasicSchedulableThread.getThread(Thread.currentThread()).getId();
				commitList.add(this);
			}
		}

		if (listeners != null)
		{
			listeners.changedValue(location, newvalue, ctxt);
		}
	}

	public static void commitAll()
	{
		synchronized (commitList)
		{
			for (TransactionValue v : commitList)
			{
				v.commit();
			}

			commitList.clear();
		}
	}

	public static void commitOne(long tid)
	{
		synchronized (commitList)
		{
			ListIterator<TransactionValue> it = commitList.listIterator();

			while (it.hasNext())
			{
				TransactionValue v = it.next();

				if (v.newthreadid == tid)
				{
					v.commit();
					it.remove();
				}
			}
		}
	}

	private void commit()
	{
		if (newthreadid > 0)
		{
			value = newvalue; // Listener called for original "set"
			newthreadid = -1;
		}

		// Experimental hood added for DESTECS
		if (Settings.dialect == Dialect.VDM_RT)
		{
			SharedStateListner.variableChanged(this, lastSetLocation);
		}
	}

	@Override
	public synchronized Object clone()
	{
		return new TransactionValue((Value) select().clone(), listeners, restrictedTo);
	}

	@Override
	public synchronized boolean isType(Class<? extends Value> valueclass)
	{
		return valueclass.isInstance(select());
	}

	@Override
	public synchronized Value deref()
	{
		return select().deref();
	}

	@Override
	public synchronized Value getConstant()
	{
		return select().getConstant();
	}

	@Override
	public synchronized boolean isUndefined()
	{
		return select().isUndefined();
	}

	@Override
	public synchronized boolean isVoid()
	{
		return select().isVoid();
	}

	@Override
	public synchronized double realValue(Context ctxt) throws ValueException
	{
		return select().realValue(ctxt);
	}

	@Override
	public synchronized long intValue(Context ctxt) throws ValueException
	{
		return select().intValue(ctxt);
	}

	@Override
	public synchronized long natValue(Context ctxt) throws ValueException
	{
		return select().nat1Value(ctxt);
	}

	@Override
	public synchronized long nat1Value(Context ctxt) throws ValueException
	{
		return select().nat1Value(ctxt);
	}

	@Override
	public synchronized boolean boolValue(Context ctxt) throws ValueException
	{
		return select().boolValue(ctxt);
	}

	@Override
	public synchronized char charValue(Context ctxt) throws ValueException
	{
		return select().charValue(ctxt);
	}

	@Override
	public synchronized ValueList tupleValue(Context ctxt)
			throws ValueException
	{
		return select().tupleValue(ctxt);
	}

	@Override
	public synchronized RecordValue recordValue(Context ctxt)
			throws ValueException
	{
		return select().recordValue(ctxt);
	}

	@Override
	public synchronized ObjectValue objectValue(Context ctxt)
			throws ValueException
	{
		return select().objectValue(ctxt);
	}

	@Override
	public synchronized String quoteValue(Context ctxt) throws ValueException
	{
		return select().quoteValue(ctxt);
	}

	@Override
	public synchronized ValueList seqValue(Context ctxt) throws ValueException
	{
		return select().seqValue(ctxt);
	}

	@Override
	public synchronized ValueSet setValue(Context ctxt) throws ValueException
	{
		return select().setValue(ctxt);
	}

	@Override
	public synchronized String stringValue(Context ctxt) throws ValueException
	{
		return select().stringValue(ctxt);
	}

	@Override
	public synchronized ValueMap mapValue(Context ctxt) throws ValueException
	{
		return select().mapValue(ctxt);
	}

	@Override
	public synchronized FunctionValue functionValue(Context ctxt)
			throws ValueException
	{
		return select().functionValue(ctxt);
	}

	@Override
	public synchronized OperationValue operationValue(Context ctxt)
			throws ValueException
	{
		return select().operationValue(ctxt);
	}

	@Override
	public synchronized boolean equals(Object other)
	{
		if (other instanceof Value)
		{
			Value val = ((Value) other).deref();

			if (val instanceof TransactionValue)
			{
				TransactionValue tvo = (TransactionValue) val;
				return select().equals(tvo.select());
			} else if (val instanceof ReferenceValue)
			{
				ReferenceValue rvo = (ReferenceValue) val;
				return select().equals(rvo.value);
			} else
			{
				return select().equals(other);
			}
		}

		return false;
	}

	@Override
	public synchronized String kind()
	{
		return select().kind();
	}

	@Override
	public synchronized int hashCode()
	{
		return select().hashCode();
	}

	@Override
	public synchronized String toString()
	{
		return select().toString();
	}
}
