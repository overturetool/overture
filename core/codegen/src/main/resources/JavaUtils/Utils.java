import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

public class Utils
{

	public static <T> List<T> seq()
	{
		return new Vector<T>();
	}
	
	public static <T> List<T> seq(T... elements)
	{
		Vector<T> vector = new Vector<T>();
		
		for (T element : elements)
		{
			vector.add(element);
		}
		
		return vector;
	}
	
	public static <T> List<T> seqTail(List<T> seq)
	{
		return seq.subList(1,seq.size());
	}
	
	public static <T> List<T> seqConc(List<T> left, List<T> right)
	{
		List<T> result = new Vector<T>();
		
		result.addAll(left);
		result.addAll(right);
		
		return result;
	}
	
	public static int hashCode(Object... fields)
	{
		int hashcode = 0;
		
		for(int i = 0; i < fields.length; i++)
		{
			Object currentField = fields[i];
			hashcode += currentField != null ? currentField.hashCode() : 0;
		}
		
		return hashcode;
	}
	
	public static Set<Long> inds(List<? extends Object> seq)
	{
		Set<Long> indices = new HashSet<Long>();
		
		for(long i = 0; i < seq.size(); i++)
		{
			indices.add(i);
		}
		
		return indices;
	}
	
	public static boolean seqEquals(List<? extends Object> left, List<? extends Object> right)
	{
		if(left == null || right == null)
			throw new IllegalArgumentException("Sequence arguments cannot be null");
		
		if(left.size() != right.size())
			return false;
		
		for(int i = 0; i < left.size(); i++)
			if(!left.get(i).equals(right.get(i)))
				return false;
				
		return true;
	}
	
	public static int index(long value)
	{
		if(value < 1)
			throw new IllegalArgumentException("VDM subscripts must be >= 1");
		
		return toInt(value) - 1;
	}
	
	private static int toInt(long value) {
	    if (value < Integer.MIN_VALUE || value > Integer.MAX_VALUE) {
	        throw new IllegalArgumentException
	            (value + " Casting long will change its value.");
	    }
	    return (int) value;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> List<T> reverse(List<T> list)
	{
		Vector<T> reversed = new Vector<T>();
		
		for(int i = list.size() - 1; i >= 0; i--)
		{
			T current = list.get(i);
			
			if(current instanceof ValueType)
			{
				ValueType valueType = ((ValueType) current).clone();
				reversed.add((T) valueType);
			}
			else
				reversed.add(current);
				
		}
		
		return reversed;
	}
	
	public static String recordToString(Record record, Object... fields)
	{
		StringBuilder str = new StringBuilder();

		str.append(fields[0]);

		for (int i = 1; i < fields.length; i++)
		{
			str.append(", " + fields[i]);
		}

		return "mk_" + record.getClass().getSimpleName() + "(" + str + ")";
	}
}
