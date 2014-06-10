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
				this.values[i] = ((ValueType) currentValue).clone();
			} else
			{
				this.values[i] = currentValue;
			}
		}
	}

	public int size()
	{
		return values.length;
	}

	public Object get(int i)
	{
		return values[i];
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == null)
			return false;

		if (this == obj)
			return true;

		if (!(obj instanceof Tuple))
			return false;

		final Tuple other = (Tuple) obj;
		if (other.size() != size())
			return false;

		final int size = size();
		for (int i = 0; i < size; i++)
		{
			final Object thisNthValue = get(i);
			final Object otherNthValue = other.get(i);
			if ((thisNthValue == null && otherNthValue != null)
					|| (thisNthValue != null && !thisNthValue.equals(otherNthValue)))
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

	public Tuple clone()
	{
		return new Tuple(values);
	}

	@Override
	public String toString()
	{
		StringBuilder str = new StringBuilder();

		str.append(values[0]);

		for (int i = 1; i < values.length; i++)
		{
			str.append(", " + values[i]);
		}

		return "mk_(" + str + ")";
	}

	@Override
	public int compareTo(Object o)
	{
		if (o instanceof Tuple)
		{
			Tuple ot = (Tuple)o;
			int diff = values.length - ot.values.length;

			if (diff != 0)
			{
				return diff;
			}
			else
			{
				for (int i=0; i < values.length; i++)
				{
					Object val = values[i];
					
					if(val instanceof Comparable)
					{
						Comparable compVal = (Comparable) val;

						@SuppressWarnings("unchecked")
						int c = compVal.compareTo(ot.values[i]);

						if (c != 0)
						{
							return c;
						}
					}
					else
						return 1;
				}

				return 0;
			}
		}
		
		if(o == null)
			return 1;

		return this.toString().compareTo(o.toString());
	}
}
