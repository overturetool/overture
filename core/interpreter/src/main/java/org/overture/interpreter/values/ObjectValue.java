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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.assistant.pattern.PTypeList;
import org.overture.ast.definitions.ASystemClassDefinition;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.lex.LexNameList;
import org.overture.ast.messages.InternalException;
import org.overture.ast.types.AClassType;
import org.overture.ast.types.PType;
import org.overture.ast.util.Utils;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.Interpreter;
import org.overture.interpreter.runtime.ObjectContext;
import org.overture.interpreter.runtime.ValueException;
import org.overture.interpreter.scheduler.Lock;
import org.overture.typechecker.util.HackLexNameToken;

public class ObjectValue extends Value
{
	private static final long serialVersionUID = 1L;

	private static int nextObjectReference = 0;

	public final int objectReference;
	public final AClassType type;
	public final NameValuePairMap members;
	public final List<ObjectValue> superobjects;

	/**
	 * The list holds all object values created by this object value
	 */
	public final List<ObjectValue> children;

	public ClassInvariantListener invlistener = null;

	public transient Lock guardLock;
	private transient CPUValue CPU;
	private Object delegateObject = null;
	private int periodicCount = 0;
	private int periodicOverlaps = 0;

	/**
	 * The Object value who created this instance
	 */
	public ObjectValue creator;

	public ObjectValue(AClassType type, NameValuePairMap members,
			List<ObjectValue> superobjects, CPUValue cpu, ObjectValue creator)
	{
		this.objectReference = getReference();
		this.type = type;
		this.members = members;
		this.superobjects = superobjects;
		this.CPU = cpu;
		this.guardLock = new Lock();
		this.children = new LinkedList<ObjectValue>();

		if (creator != null)
		{
			setCreator(creator);
		}

		setSelf(this);
	}

	private static synchronized int getReference()
	{
		return ++nextObjectReference;
	}

	private void setSelf(ObjectValue self)
	{
		for (NameValuePair nvp : members.asList())
		{
			Value deref = nvp.value.deref();

			if (deref instanceof OperationValue)
			{
				OperationValue ov = (OperationValue) deref;
				ov.setSelf(self);
			} else if (deref instanceof FunctionValue)
			{
				FunctionValue fv = (FunctionValue) deref;
				fv.setSelf(self);
			} else if (deref instanceof ObjectValue)
			{
				((ObjectValue) deref).setCreator(self);
			}
		}

		for (ObjectValue obj : superobjects)
		{
			obj.setSelf(self);
		}
	}

	@Override
	public ObjectValue objectValue(Context ctxt)
	{
		return this;
	}
	
	public PType getType()
	{
		return type;
	}

	public PTypeList getBaseTypes()
	{
		PTypeList basetypes = new PTypeList();

		if (superobjects.isEmpty())
		{
			basetypes.add(type);
		} else
		{
			for (ObjectValue sup : superobjects)
			{
				basetypes.addAll(sup.getBaseTypes());
			}
		}

		return basetypes;
	}

	@Override
	public Value getUpdatable(ValueListenerList listeners)
	{
		for (Entry<ILexNameToken, Value> m: members.entrySet())
		{
			Value v = m.getValue();
			
			if (v.deref() instanceof ObjectValue)
			{
				// Don't recurse into inner objects, just mark field itself
				m.setValue(UpdatableValue.factory(v, listeners));
			}
			else if (v.deref() instanceof FunctionValue)
			{
				// Ignore function members
			}
			else if (v.deref() instanceof OperationValue)
			{
				// Ignore operation members
			}
			else
			{
				m.setValue(v.getUpdatable(listeners));
			}
		}

		return UpdatableValue.factory(this, listeners);
	}

	public OperationValue getThreadOperation(Context ctxt)
			throws ValueException
	{
		return get(type.getClassdef().getName().getThreadName(), false).operationValue(ctxt);
	}

	public synchronized int incPeriodicCount()
	{
		if (periodicCount > 0)
		{
			periodicOverlaps++;
		}

		periodicCount++;
		return periodicOverlaps;
	}

	public synchronized void decPeriodicCount()
	{
		periodicCount--;
	}

	public synchronized Value get(ILexNameToken field, boolean explicit)
	{
		ILexNameToken localname = explicit ? field
				: field.getModifiedName(type.getName().getName());

		// This is another case where we have to iterate with equals()
		// rather than using the map's hash, because the hash doesn't
		// take account of the TypeComparator looseness when comparing
		// qualified names. Not very efficient... so we try a raw get
		// first.

		Value rv = members.get(localname);

		if (rv == null)
		{
			for (ILexNameToken var : members.keySet())
			{
				if (HackLexNameToken.isEqual(var, localname))
				{
					rv = members.get(var);
					break;
				}
			}
		}

		if (rv != null)
		{
			return rv;
		}

		for (ObjectValue svalue : superobjects)
		{
			rv = svalue.get(field, explicit);

			if (rv != null)
			{
				return rv;
			}
		}

		return null;
	}

	public ValueList getOverloads(ILexNameToken field)
	{
		ValueList list = new ValueList();

		// This is another case where we have to iterate with matches()
		// rather than using the map's hash, because the hash includes the
		// overloaded type qualifiers...

		for (ILexNameToken var : members.keySet())
		{
			if (var.matches(field)) // Ignore type qualifiers
			{
				list.add(members.get(var));
			}
		}

		if (!list.isEmpty())
		{
			return list; // Only names from one level
		}

		for (ObjectValue svalue : superobjects)
		{
			list = svalue.getOverloads(field);

			if (!list.isEmpty())
			{
				return list;
			}
		}

		return list;
	}

	public NameValuePairMap getMemberValues()
	{
		NameValuePairMap nvpm = new NameValuePairMap();

		for (ObjectValue svalue : superobjects)
		{
			nvpm.putAll(svalue.getMemberValues());
		}

		nvpm.putAll(members);
		return nvpm;
	}

	@Override
	public boolean equals(Object other)
	{
		if (other instanceof Value)
		{
			Value val = ((Value) other).deref();

			if (val instanceof ObjectValue)
			{
    			return ((ObjectValue) val).objectReference == this.objectReference;
			}
		}

		return false;
	}

	private boolean inToString = false;

	@Override
	public String toString()
	{
		if (inToString)
		{
			return "{#" + objectReference + " recursive}";
		}

		inToString = true;
		StringBuilder sb = new StringBuilder();
		sb.append(type.toString());
		sb.append("{#" + objectReference);

		for (ILexNameToken name : members.keySet())
		{
			Value ov = members.get(name);
			Value v = ov.deref();

			if (!(v instanceof FunctionValue) && !(v instanceof OperationValue))
			{
				sb.append(", ");
				sb.append(name.getName());

				if (ov instanceof UpdatableValue)
				{
					sb.append(":=");
				} else
				{
					sb.append("=");
				}

				sb.append(v.toString());
			}
		}

		if (!superobjects.isEmpty())
		{
			sb.append(", ");
			sb.append(Utils.listToString(superobjects));
		}

		sb.append("}");
		inToString = false;
		return sb.toString();
	}

	@Override
	public int hashCode()
	{
		// return type.hashCode() + objectReference + superobjects.hashCode();
		return objectReference;
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
		Value conv = convertToHierarchy(to);

		if (conv != null)
		{
			return conv;
		}

		// This will fail...
		return super.convertValueTo(to, ctxt, done);
	}

	private Value convertToHierarchy(PType to)
	{
		if (to.equals(type))
		{
			return this;
		}

		for (ObjectValue svalue : superobjects)
		{
			Value conv = svalue.convertToHierarchy(to);

			if (conv != null)
			{
				return this; // NB. not the subtype
			}
		}

		return null;
	}

	@Override
	public Object clone()
	{
		return deepCopy();
	}

	private ObjectValue mycopy = null;

	@Override
	public ObjectValue shallowCopy()
	{
		if (mycopy != null)
		{
			return mycopy;
		}

		mycopy = new ObjectValue(type, new NameValuePairMap(), new Vector<ObjectValue>(), CPU, creator);

		List<ObjectValue> supers = mycopy.superobjects;
		NameValuePairMap memcopy = mycopy.members;

		for (ObjectValue sobj : superobjects)
		{
			supers.add( // Type skeleton only...
			new ObjectValue(sobj.type, new NameValuePairMap(), new Vector<ObjectValue>(), sobj.CPU, creator));
		}

		for (ILexNameToken name : members.keySet())
		{
			Value mv = members.get(name);

			if (mv.deref() instanceof ObjectValue)
			{
				ObjectValue om = (ObjectValue) mv.deref();
				memcopy.put(name, om.shallowCopy());
			} else
			{
				memcopy.put(name, (Value) mv.clone());
			}
		}

		mycopy.setSelf(mycopy);

		ObjectValue rv = mycopy;
		mycopy = null;
		return rv;
	}

	@Override
	public ObjectValue deepCopy()
	{
		try
		{
			// This is slow, but it has the advantage that Value copies,
			// such as the parent and subclass copies of the same
			// variable, are preserved as the same variable rather than
			// being split, as they are in naive object copies.

			ByteArrayOutputStream os = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(os);
			oos.writeObject(this);
			oos.close();

			ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
			ObjectInputStream ois = new ObjectInputStream(is);
			ObjectValue result = (ObjectValue) ois.readObject();

			result.setSelf(result);
			return result;
		} catch (Exception e)
		{
			throw new InternalException(5, "Illegal clone: " + e);
		}
	}

	public MapValue getOldValues(LexNameList oldnames)
	{
		ValueMap values = new ValueMap();
		ObjectContext ctxt = new ObjectContext(Interpreter.getInstance().getAssistantFactory(), type.getLocation(), "Old Object Creation", null, this);

		for (ILexNameToken name : oldnames)
		{
			Value mv = ctxt.check(name.getNewName()).deref();
			SeqValue sname = new SeqValue(name.getName());

			if (mv instanceof ObjectValue)
			{
				ObjectValue om = (ObjectValue) mv;
				values.put(sname, om.deepCopy());
			} else
			{
				values.put(sname, (Value) mv.clone());
			}
		}

		return new MapValue(values);
	}

	// private void writeObject(ObjectOutputStream out)
	// throws IOException
	// {
	// out.defaultWriteObject();
	// }

	private void readObject(ObjectInputStream in)
			throws ClassNotFoundException, IOException
	{
		in.defaultReadObject();
		CPU = CPUValue.vCPU;
		guardLock = new Lock();
	}

	public synchronized void setCPU(CPUValue cpu)
	{
		CPU = cpu;
	}

	public synchronized CPUValue getCPU()
	{
		return CPU == null ? CPUValue.vCPU : CPU;
	}

	public boolean hasDelegate(Context ctxt)
	{
		if (ctxt.assistantFactory.createSClassDefinitionAssistant().hasDelegate(type.getClassdef()))
		{
			if (delegateObject == null)
			{
				delegateObject = ctxt.assistantFactory.createSClassDefinitionAssistant().newInstance(type.getClassdef());
			}

			return true;
		}

		return false;
	}

	public Value invokeDelegate(Context ctxt)
	{
		return ctxt.assistantFactory.createSClassDefinitionAssistant().invokeDelegate(type.getClassdef(), delegateObject, ctxt);
	}

	public static void init()
	{
		nextObjectReference = 0;
	}

	public void setListener(ClassInvariantListener listener)
	{
		invlistener = listener;
		listener.invopvalue.setSelf(this);
	}

	/**
	 * Sets the creator of this object value and adds this to the newCreator parsed as argument
	 * 
	 * @param newCreator
	 *            The creator of this object value
	 */
	private synchronized void setCreator(ObjectValue newCreator)
	{
		// Do not set the creator if created by the system class. The System contains
		// fields with references to Thread instances which are not Serializable and
		// will fail a deep copy with a NotSerializableExpection for Thread
		if (newCreator != null
				&& newCreator.type.getClassdef() instanceof ASystemClassDefinition)
		{
			return;
		}
		// establish transitive reference
		newCreator.addChild(this);
	}

	/**
	 * Removed the creator of this object value by detaching from the creator's child list
	 */
	public synchronized void removeCreator()
	{
		// if we are moving to a new CPU, we are no longer a part of the transitive
		// references from our creator, so let us remove ourself. This will prevent
		// us from being updated if our creator is migrating in the
		// future.
		if (this.creator != null)
		{
			this.creator.removeChild(this);
			// creator no longer needed, as we already detached ourself.
			this.creator = null;
		}
	}

	/**
	 * Add a child created by this object value
	 * 
	 * @param referenced
	 */
	private synchronized void addChild(ObjectValue referenced)
	{
		children.add(referenced);
	}

	/**
	 * Remove a child from this object value. After this the reference will no longer be considered as created by this
	 * object value
	 * 
	 * @param reference
	 */
	private synchronized void removeChild(ObjectValue reference)
	{
		children.remove(reference);
	}
}
