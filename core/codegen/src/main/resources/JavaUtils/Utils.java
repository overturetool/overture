import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

public class Utils
{
	public static List seq()
	{
		return new Vector();
	}
	
	public static List seq(Object... elements)
	{
		if(elements == null)
			throw new IllegalArgumentException("Cannot instantiate sequence from null");
		
		return addAll(seq(), elements);
	}
	
	private static List addAll(List to, Object... from)
	{
		if(to == null || from == null)
			throw new IllegalArgumentException("Arguments to addAll cannot be null");
		
		for(Object element : from)
		{
			if(element instanceof ValueType)
			{
				ValueType valueType = (ValueType) element;
				element = valueType.clone();
			}
			
			to.add(element);
		}
		
		return to;
	}
	
	public static List reverse(List seq)
	{
		if(seq == null)
			throw new IllegalArgumentException("Cannot reverse null");
		
		List result = seq();
		
		addAll(result, seq);

		Collections.reverse(result);
		
		return result;
	}
	
	public static List seqTail(List seq)
	{
		if(seq == null)
			throw new IllegalArgumentException("Cannot take tail of null");
		
		return seq.subList(1,seq.size());
	}
	
	public static boolean seqEquals(List left, List right)
	{
		if(left == null || right == null)
			throw new IllegalArgumentException("A sequences cannot be compared to null");
		
		if(left.size() != right.size())
			return false;
		
		for(int i = 0; i < left.size(); i++)
			if(!left.get(i).equals(right.get(i)))
				return false;
				
		return true;
	}
	
	public static List seqConc(List left, List right)
	{
		if(left == null || right == null)
			throw new IllegalArgumentException("Cannot concatenate null");

		List result = seq();

		addAll(result, left.toArray());
		addAll(result, right.toArray());
		
		return result;
	}
	
	public static List distConc(List... seqs)
	{
		List result = seq();
		
		for(List seq : seqs)
		{
			addAll(result, seq.toArray());
		}
		
		return result;
	}
	
	public static String distConcStrings(String... strings)
	{
		String result = "";
		
		for(String str : strings)
			result += str;
		
		return result;
	}
	
	public static int hashCode(Object... fields)
	{
		if(fields == null)
			throw new IllegalArgumentException("Fields cannot be null");

		int hashcode = 0;
		
		for(int i = 0; i < fields.length; i++)
		{
			Object currentField = fields[i];
			hashcode += currentField != null ? currentField.hashCode() : 0;
		}
		
		return hashcode;
	}
	
	public static Set<Long> inds(List seq)
	{
		if(seq == null)
			throw new IllegalArgumentException("Cannot get indices of null");
		
		Set<Long> indices = new HashSet<Long>();
		
		for(long i = 0; i < seq.size(); i++)
		{
			indices.add(i);
		}
		
		return indices;
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
		
	public static String recordToString(Record record, Object... fields)
	{
		if(record == null)
			throw new IllegalArgumentException("Record cannot be null in recordToString");
		
		if(fields == null)
			throw new IllegalArgumentException("Fields cannot be null in recordToString");
		
		StringBuilder str = new StringBuilder();

		str.append(fields[0]);

		for (int i = 1; i < fields.length; i++)
		{
			str.append(", " + fields[i]);
		}

		return "mk_" + record.getClass().getSimpleName() + "(" + str + ")";
	}
}
