/*
 * The source of inspiration for the implementation of this class:
 * https://github.com/ripper234/Basic/tree/master/java/src/main/java/org/basic/datastructures/tuple
 */
public class Tuple
{
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

			if (currentValue instanceof Tuple)
			{
				this.values[i] = ((Tuple) currentValue).clone();
			} else if (currentValue instanceof Record)
			{
				this.values[i] = ((Record) currentValue).clone();
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

	@SuppressWarnings("unchecked")
	public <T> T getNthValue(int i)
	{
		return (T) values[i];
	}

	@Override
	public boolean equals(Object object)
	{
		if (object == null)
			return false;

		if (this == object)
			return true;

		if (!(object instanceof Tuple))
			return false;

		final Tuple other = (Tuple) object;
		if (other.size() != size())
			return false;

		final int size = size();
		for (int i = 0; i < size; i++)
		{
			final Object thisNthValue = getNthValue(i);
			final Object otherNthValue = other.getNthValue(i);
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
}
