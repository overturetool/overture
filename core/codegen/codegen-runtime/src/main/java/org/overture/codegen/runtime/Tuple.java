/*
 * #%~
 * VDM Code Generator Runtime
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
package org.overture.codegen.runtime;

/*
 * The source of inspiration for the implementation of this class:
 * https://github.com/ripper234/Basic/tree/master/java/src/main/java/org/basic/datastructures/tuple
 */
@SuppressWarnings("rawtypes")
public class Tuple implements ValueType, Comparable
{
	private static final long serialVersionUID = 8013562414260810880L;

	private final Object[] values;

	public static Tuple mk_(Object... values)
	{
		return new Tuple(values);
	}

	private Tuple(Object[] values)
	{
		if (values == null || values.length < 2)
		{
			throw new IllegalArgumentException("A tuple can only have two or more values");
		} else
		{

			this.values = new Object[values.length];
			init(values);
		}
	}

	private void init(Object[] initvalues)
	{
		for (int i = 0; i < initvalues.length; i++)
		{
			Object currentValue = initvalues[i];

			if (currentValue instanceof ValueType)
			{
				this.values[i] = ((ValueType) currentValue).copy();
			} else
			{
				this.values[i] = currentValue;
			}
		}
	}

	public Long size()
	{
		return (long) values.length;
	}

	public Object get(int i)
	{
		return values[i];
	}

	public boolean compatible(Class... types)
	{
		if (this.values.length != types.length)
		{
			return false;
		}

		for (int i = 0; i < this.values.length; i++)
		{
			Object toValue = this.values[i];
			Class type = types[i];

			if (type == null)
			{
				return false;
			}

			if (toValue instanceof VDMSeq && type == String.class)
			{
				for (Object c : (VDMSeq) toValue)
				{
					if (!(c instanceof Character))
					{
						return false;
					}
				}

				return true;
			}

			if (toValue instanceof String && type == VDMSeq.class)
			{
				return true;
			}

			if (toValue != null && !type.isInstance(toValue))
			{
				return false;
			}
		}

		return true;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == null)
		{
			return false;
		}

		if (this == obj)
		{
			return true;
		}

		if (!(obj instanceof Tuple))
		{
			return false;
		}

		final Tuple other = (Tuple) obj;
		if (other.size() != size())
		{
			return false;
		}

		final int size = values.length;
		for (int i = 0; i < size; i++)
		{
			final Object thisNthValue = get(i);
			final Object otherNthValue = other.get(i);
			if (thisNthValue == null && otherNthValue != null
					|| thisNthValue != null
							&& !Utils.equals(thisNthValue, otherNthValue))
			{
				return false;
			}
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		int hash = 17;
		for (Object value : values)
		{
			if (value != null)
			{
				hash = hash * 37 + value.hashCode();
			}
		}
		return hash;
	}

	public Tuple copy()
	{
		return new Tuple(values);
	}

	@Override
	public String toString()
	{
		StringBuilder str = new StringBuilder();

		str.append(Utils.toString(values[0]));

		for (int i = 1; i < values.length; i++)
		{
			str.append(", " + Utils.toString(values[i]));
		}

		return "mk_(" + str + ")";
	}

	@Override
	public int compareTo(Object o)
	{
		if (o instanceof Tuple)
		{
			Tuple ot = (Tuple) o;
			int diff = values.length - ot.values.length;

			if (diff != 0)
			{
				return diff;
			} else
			{
				for (int i = 0; i < values.length; i++)
				{
					Object val = values[i];

					if (val instanceof Comparable)
					{
						Comparable compVal = (Comparable) val;

						@SuppressWarnings("unchecked")
						int c = compVal.compareTo(ot.values[i]);

						if (c != 0)
						{
							return c;
						}
					} else
					{
						return 1;
					}
				}

				return 0;
			}
		}

		if (o == null)
		{
			return 1;
		}

		return this.toString().compareTo(o.toString());
	}
}
