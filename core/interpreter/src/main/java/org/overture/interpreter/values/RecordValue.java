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

import java.util.Iterator;
import java.util.Set;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.types.AFieldField;
import org.overture.ast.types.ARecordInvariantType;
import org.overture.ast.types.PType;
import org.overture.config.Settings;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.Interpreter;
import org.overture.interpreter.runtime.ValueException;

public class RecordValue extends Value
{
	private static final long serialVersionUID = 1L;
	public final ARecordInvariantType type;
	public final FieldMap fieldmap;
	public final FunctionValue invariant;
	public final FunctionValue equality;
	public final FunctionValue ordering;

	// mk_ expressions
	public RecordValue(ARecordInvariantType type, ValueList values, Context ctxt)
			throws AnalysisException
	{
		this.type = type;
		this.fieldmap = new FieldMap();
		this.invariant = ctxt.assistantFactory.createSInvariantTypeAssistant().getInvariant(type, ctxt);
		this.equality = ctxt.assistantFactory.createSInvariantTypeAssistant().getEquality(type, ctxt);
		this.ordering = ctxt.assistantFactory.createSInvariantTypeAssistant().getOrder(type, ctxt);

		if (values.size() != type.getFields().size())
		{
			abort(4078, "Wrong number of fields for " + type.getName(), ctxt);
		}

		Iterator<AFieldField> fi = type.getFields().iterator();

		for (Value v : values)
		{
			AFieldField f = fi.next();
			fieldmap.add(f.getTag(), v.convertTo(f.getType(), ctxt), !f.getEqualityAbstraction());
		}

		checkInvariant(ctxt);
	}

	// mu_ expressions
	public RecordValue(ARecordInvariantType type, FieldMap mapvalues,
			Context ctxt) throws AnalysisException
	{
		this.type = type;
		this.fieldmap = new FieldMap();
		this.invariant = ctxt.assistantFactory.createSInvariantTypeAssistant().getInvariant(type, ctxt);
		this.equality = ctxt.assistantFactory.createSInvariantTypeAssistant().getEquality(type, ctxt);
		this.ordering = ctxt.assistantFactory.createSInvariantTypeAssistant().getOrder(type, ctxt);

		if (mapvalues.size() != type.getFields().size())
		{
			abort(4080, "Wrong number of fields for " + type.getName(), ctxt);
		}

		Iterator<AFieldField> fi = type.getFields().iterator();

		while (fi.hasNext())
		{
			AFieldField f = fi.next();
			Value v = mapvalues.get(f.getTag());

			if (v == null)
			{
				abort(4081, "Field not defined: " + f.getTag(), ctxt);
			}

			fieldmap.add(f.getTag(), v.convertTo(f.getType(), ctxt), !f.getEqualityAbstraction());
		}

		checkInvariant(ctxt);
	}

	// Only called by clone()
	private RecordValue(ARecordInvariantType type, FieldMap mapvalues,
						FunctionValue invariant, FunctionValue equality, FunctionValue ordering)
	{
		this.type = type;
		this.invariant = invariant;
		this.fieldmap = mapvalues;
		this.equality = equality;
		this.ordering = ordering;
	}

	public RecordValue(ARecordInvariantType type, NameValuePairList mapvalues,
			Context ctxt)
	{
		this.type = type;
		this.invariant = null;
		this.fieldmap = new FieldMap();
		this.equality = null;
		this.ordering = null;

		for (NameValuePair nvp : mapvalues)
		{
			AFieldField f = ctxt.assistantFactory.createARecordInvariantTypeAssistant().findField(type, nvp.name.getName());
			this.fieldmap.add(nvp.name.getName(), nvp.value, !f.getEqualityAbstraction());
		}
	}

	@Override
	public boolean isOrdered()
	{
		return ordering != null;
	}

	public void checkInvariant(Context ctxt) throws AnalysisException
	{
		if (invariant != null && Settings.invchecks)
		{
			// In VDM++ and VDM-RT, we do not want to do thread swaps half way
			// through an invariant check, so we set the atomic flag around the
			// conversion. This also stops VDM-RT from performing "time step"
			// calculations.

			try
			{
				ctxt.threadState.setAtomic(true);
				boolean inv = invariant.eval(invariant.location, this, ctxt).boolValue(ctxt);

				if (!inv)
				{
					abort(4079, "Type invariant violated by mk_" + type.getName()+ " arguments", ctxt);
				}
			} finally
			{
				ctxt.threadState.setAtomic(false);
			}
		}
	}

	@Override
	public RecordValue recordValue(Context ctxt)
	{
		return this;
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

		FieldMap nm = new FieldMap();

		for (FieldValue fv : fieldmap)
		{
			Value uv = fv.value.getUpdatable(listeners);
			nm.add(fv.name, uv, fv.comparable);
		}

		UpdatableValue uval = UpdatableValue.factory(new RecordValue(type, nm, invariant, equality, ordering), listeners);

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
		FieldMap nm = new FieldMap();

		for (FieldValue fv : fieldmap)
		{
			Value uv = fv.value.getConstant();
			nm.add(fv.name, uv, fv.comparable);
		}

		return new RecordValue(type, nm, invariant, equality, ordering);
	}

	@Override
	public boolean equals(Object other)
	{
		if (other instanceof Value)
		{
			Value val = ((Value)other).deref();

			if (val instanceof RecordValue)
			{
				RecordValue ot = (RecordValue)val;

				if (ot.type.equals(type))
				{
					if (equality != null)
					{
						Context ctxt = Interpreter.getInstance().initialContext;
						ctxt.setThreadState(null, null);
						ctxt.threadState.setAtomic(true);

						try
						{
							ValueList args = new ValueList();
							args.add(this);
							args.add(ot);
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
						for (AFieldField f: type.getFields())
						{
							if (!f.getEqualityAbstraction())
							{
								Value fv = fieldmap.get(f.getTag());
								Value ofv = ot.fieldmap.get(f.getTag());

								if (fv == null || ofv == null)
								{
									return false;
								}

								if (!fv.equals(ofv))
								{
									return false;
								}
							}
						}
					}

					return true;
				}
			}
		}

		return false;
	}

	@Override
	public int compareTo(Value other)
	{
		Value val = other.deref();

		if (val instanceof RecordValue)
		{
			RecordValue ot = (RecordValue)val;

			if (ot.type.equals(type))
			{
				if (ordering != null)
				{
					Context ctxt = Interpreter.getInstance().initialContext;
					ctxt.setThreadState(null, null);
					ctxt.threadState.setAtomic(true);

					try
					{
						ValueList args = new ValueList();
						args.add(this);
						args.add(ot);

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
				else
				{
					for (AFieldField f: type.getFields())
					{
						if (!f.getEqualityAbstraction())
						{
							Value fv = fieldmap.get(f.getTag());
							Value ofv = ot.fieldmap.get(f.getTag());

							if (fv == null || ofv == null)
							{
								return -1;
							}

							int comp = fv.compareTo(ofv);

							if (comp != 0)
							{
								return comp;
							}
						}
					}

					return 0;
				}
			}
		}

		return super.compareTo(other);	// Indicates incomparable values, but allows "sorting"
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("mk_" + type.getName() + "(");

		Iterator<AFieldField> fi = type.getFields().iterator();

		if (fi.hasNext())
		{
			String ftag = fi.next().getTag();
			sb.append(fieldmap.get(ftag));

			while (fi.hasNext())
			{
				ftag = fi.next().getTag();
				sb.append(", " + fieldmap.get(ftag));
			}
		}

		sb.append(")");
		return sb.toString();
	}

	@Override
	public int hashCode()
	{
		return type.getName().hashCode() + fieldmap.hashCode();
	}

	@Override
	public String kind()
	{
		return type.toString();
	}

	@Override
	protected Value convertValueTo(PType to, Context ctxt, Set<PType> done)
			throws AnalysisException
	{
		if (ctxt.assistantFactory.createPTypeAssistant().equals(to, type))
		{
			return this;
		} else
		{
			return super.convertValueTo(to, ctxt, done);
		}
	}

	@Override
	public Object clone()
	{
		return new RecordValue(type, (FieldMap) fieldmap.clone(), invariant, equality, ordering);
	}
}
