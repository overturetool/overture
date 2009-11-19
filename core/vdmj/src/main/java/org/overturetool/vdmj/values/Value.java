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

import java.io.Serializable;
import java.util.Formattable;
import java.util.FormattableFlags;
import java.util.Formatter;

import org.overturetool.vdmj.Settings;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.ValueException;
import org.overturetool.vdmj.types.BracketType;
import org.overturetool.vdmj.types.NamedType;
import org.overturetool.vdmj.types.OptionalType;
import org.overturetool.vdmj.types.ParameterType;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.UnionType;
import org.overturetool.vdmj.types.UnknownType;

/**
 * The parent of all runtime values.
 */

abstract public class Value implements Comparable<Value>, Serializable, Formattable
{
	private static final long serialVersionUID = 1L;

	@Override
	abstract public String toString();

	// This is overridden in the few classes that need to change formatting
	public void formatTo(Formatter formatter, int flags, int width, int precision)
	{
		formatTo(this.toString(), formatter, flags, width, precision);
	}

	protected void formatTo(String value, Formatter formatter, int flags, int width, int precision)
	{
		StringBuilder sb = new StringBuilder("%");

		switch (flags)
		{
			case FormattableFlags.LEFT_JUSTIFY:
				sb.append('-');
				break;

			case FormattableFlags.ALTERNATE:
				sb.append('#');
				break;
		}

		if (width > 0)
		{
			sb.append(width);
		}

		if (precision > 0)
		{
			sb.append('.');
			sb.append(precision);
		}

		sb.append('s');

		formatter.format(sb.toString(), value);
	}

	@Override
	abstract public boolean equals(Object other);

	@Override
	abstract public int hashCode();

	/** A string with the informal kind of the value, like "set". */
	abstract public String kind();

	@Override
	abstract public Object clone();

	public Value deepCopy()
	{
		return (Value)clone();
	}

	public Value shallowCopy()
	{
		return (Value)clone();
	}

	public String toShortString(int max)
	{
		String value = toString();

		if (value.length() > max)
		{
			value = value.substring(0, max/2) +
				"..." + value.substring(value.length() - max/2);
		}

		return value;
	}

	/**
	 * Performing a dynamic type conversion. This method is usually specialized
	 * by subclasses that know how to convert themselves to other types. If they
	 * fail, they delegate the conversion up to this superclass method which
	 * deals with the special cases: unions, type parameters, optional types,
	 * bracketed types and named types. If all these also fail, the method throws
	 * a runtime dynamic type check exception - though that may be caught, for
	 * example by the union processing, as it iterates through the types in the
	 * union given, trying to convert the value.
	 *
	 * @param to The target type.
	 * @param ctxt The context in which to make the conversion.
	 * @return This value converted to the target type.
	 *
	 * @throws ValueException Cannot perform the type conversion.
	 */

	public Value convertTo(Type to, Context ctxt) throws ValueException
	{
		if (Settings.dynamictypechecks)
		{
			return convertValueTo(to, ctxt);
		}
		else
		{
			return this;	// Good luck!!
		}
	}

	public Value convertValueTo(Type to, Context ctxt) throws ValueException
	{
		if (to instanceof UnionType)
		{
			UnionType uto = (UnionType)to;

			for (Type ut: uto.types)
			{
				try
				{
					return convertValueTo(ut, ctxt);
				}
				catch (ValueException e)
				{
					// Union type not applicable
				}
			}
		}
		else if (to instanceof ParameterType)
		{
			ParameterType pt = (ParameterType)to;

			// Parameter types are ParameterValues of the given name in
			// the context.

			Value v = ctxt.lookup(pt.name);

			if (v instanceof ParameterValue)
			{
				ParameterValue pv = (ParameterValue)v;
				return convertValueTo(pv.type, ctxt);
			}

			abort(4086, "Value of type parameter is not a type", ctxt);
		}
		else if (to instanceof OptionalType)
		{
			OptionalType ot = (OptionalType)to;
			return convertValueTo(ot.type, ctxt);
		}
		else if (to instanceof BracketType)
		{
			BracketType bt = (BracketType)to;
			return convertValueTo(bt.type, ctxt);
		}
		else if (to instanceof NamedType)
		{
			NamedType ntype = (NamedType)to;
			Value converted = convertValueTo(ntype.type, ctxt);
			return new InvariantValue(ntype, converted, ctxt);
		}
		else if (to instanceof UnknownType)
		{
			return this;	// Suppressing DTC for "?" types
		}

		abort(4087, "Cannot convert " + toShortString(100) + " (" + kind() + ") to " + to, ctxt);
		return null;
	}

	/**
	 * Change the object's value. Normally, values are immutable, but subclasses
	 * of {@link UpdatableValue} implement this set method to replace the object
	 * referenced with another. ReferenceValues like UpdatableValue delegate all
	 * the other Value method to the contained object.
	 *
	 * @param newval The new value to set
	 * @param ctxt The context used
	 * @throws ValueException
	 */

	public void set(
		@SuppressWarnings("unused") LexLocation location, Value newval, Context ctxt)
		throws ValueException
	{
		abort(4088, "Set not permitted for " + kind(), ctxt);
	}

	public Value abort(int number, String msg, Context ctxt) throws ValueException
	{
		throw new ValueException(number, msg, ctxt);
	}

	public Value abort(int number, Exception e, Context ctxt) throws ValueException
	{
		throw new ValueException(number, e.getMessage(), ctxt);
	}

	public boolean isUndefined()
	{
		return false;
	}

	public boolean isVoid()
	{
		return false;
	}

	public boolean isNumeric()
	{
		return this instanceof NumericValue;
	}

	public boolean isType(Class<? extends Value> valueclass)
	{
		return valueclass.isInstance(this);
	}

	public Value deref()
	{
		return this;	// ReferenceValues are dereferenced
	}

	/**
	 * Return an UpdatableValue, wrapping this one. This is a deep translation
	 * that recurses into all Values that contain other Values (sets etc),
	 * converting their contents to UpdateableValues. The results can then be
	 * modified in assignment statements or used as state data etc.
	 *
	 * @param listener The listener to inform of updates to the value.
	 * @return An UpdatableValue for this one.
	 */

	public Value getUpdatable(ValueListener listener)
	{
		return UpdatableValue.factory(this, listener);
	}

	/**
	 * The method for the comparable interface. This is only implemented by
	 * numeric types, and allows collections of them to be sorted. By default,
	 * the method compares the string form of the values, which gives an
	 * arbitrary, but fixed order for such values.
	 *
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */

	public int compareTo(Value other)
	{
		return toString().compareTo(other.toString());
	}

	public Value sorted()
	{
		// Sort sub-elements into a standard order, where appropriate.
		return this;
	}

	public double realValue(Context ctxt) throws ValueException
	{
		abort(4089, "Can't get real value of " + kind(), ctxt);
		return 0;
	}

	public double ratValue(Context ctxt) throws ValueException
	{
		abort(4090, "Can't get rat value of " + kind(), ctxt);
		return 0;
	}

	public long intValue(Context ctxt) throws ValueException
	{
		abort(4091, "Can't get int value of " + kind(), ctxt);
		return 0;
	}

	public long natValue(Context ctxt) throws ValueException
	{
		abort(4092, "Can't get nat value of " + kind(), ctxt);
		return 0;
	}

	public long nat1Value(Context ctxt) throws ValueException
	{
		abort(4093, "Can't get nat1 value of " + kind(), ctxt);
		return 0;
	}

	public boolean boolValue(Context ctxt) throws ValueException
	{
		abort(4094, "Can't get bool value of " + kind(), ctxt);
		return false;
	}

	public char charValue(Context ctxt) throws ValueException
	{
		abort(4095, "Can't get char value of " + kind(), ctxt);
		return 0;
	}

	public ValueList tupleValue(Context ctxt) throws ValueException
	{
		abort(4096, "Can't get tuple value of " + kind(), ctxt);
		return null;
	}

	public RecordValue recordValue(Context ctxt) throws ValueException
	{
		abort(4097, "Can't get record value of " + kind(), ctxt);
		return null;
	}

	public String quoteValue(Context ctxt) throws ValueException
	{
		abort(4098, "Can't get quote value of " + kind(), ctxt);
		return null;
	}

	public ValueList seqValue(Context ctxt) throws ValueException
	{
		abort(4099, "Can't get sequence value of " + kind(), ctxt);
		return null;
	}

	public ValueSet setValue(Context ctxt) throws ValueException
	{
		abort(4100, "Can't get set value of " + kind(), ctxt);
		return null;
	}

	public String stringValue(Context ctxt) throws ValueException
	{
		abort(4101, "Can't get string value of " + kind(), ctxt);
		return null;
	}

	public ValueMap mapValue(Context ctxt) throws ValueException
	{
		abort(4102, "Can't get map value of " + kind(), ctxt);
		return null;
	}

	public FunctionValue functionValue(Context ctxt) throws ValueException
	{
		abort(4103, "Can't get function value of " + kind(), ctxt);
		return null;
	}

	public OperationValue operationValue(Context ctxt) throws ValueException
	{
		abort(4104, "Can't get operation value of " + kind(), ctxt);
		return null;
	}

	public ObjectValue objectValue(Context ctxt) throws ValueException
	{
		abort(4105, "Can't get object value of " + kind(), ctxt);
		return null;
	}
}
