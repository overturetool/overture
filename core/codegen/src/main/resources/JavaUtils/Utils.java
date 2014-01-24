import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

public class Utils
{
	public static VDMSeq seq()
	{
		return new VDMSeq();
	}
	
	public static VDMSeq seq(Object... elements)
	{
		if(elements == null)
			throw new IllegalArgumentException("Cannot instantiate sequence from null");
		
		return addAll(seq(), elements);
	}
	
	public static Map map()
	{
		return new HashMap();
	}
	
	public static Map map(Maplet... elements)
	{
		if(elements == null)
			throw new IllegalArgumentException("Cannot instantiate map from null");
		
		return putAll(map(), elements);
	}
	
	public static Map putAll(Map to, Maplet... from)
	{
		if(to == null || from == null)
			throw new IllegalArgumentException("Arguments to putAll cannot be null");

		for (Maplet maplet : from)
		{
			Object left = maplet.getLeft();
			Object right = maplet.getRight();
			
			if (left instanceof ValueType)
			{
				ValueType valueType = (ValueType) left;
				left = valueType.clone();
			}
			
			if(right instanceof ValueType)
			{
				ValueType valueType = (ValueType) right;
				right = valueType.clone();
			}
			
			to.put(left ,right);
		}
		
		return to;
	}
	
	private static VDMSeq addAll(VDMSeq to, Object... from)
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
	
	public static VDMSeq reverse(VDMSeq seq)
	{
		if(seq == null)
			throw new IllegalArgumentException("Cannot reverse null");
		
		VDMSeq result = seq();
		
		addAll(result, seq);

		Collections.reverse(result);
		
		return result;
	}
	
	public static VDMSeq seqTail(VDMSeq seq)
	{
		if(seq == null)
			throw new IllegalArgumentException("Cannot take tail of null");
		
		if(seq.isEmpty())
			throw new IllegalArgumentException("Cannot take tail of empty sequence");
		
		VDMSeq tail = new VDMSeq();
		
		for(int i = 1; i < seq.size(); i++)
		{
			Object element = seq.get(i);
			tail.add(element);
		}
		
		return tail;
	}
	
	public static boolean seqEquals(VDMSeq left, VDMSeq right)
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
	
	public static VDMSeq seqConc(VDMSeq left, VDMSeq right)
	{
		if(left == null || right == null)
			throw new IllegalArgumentException("Cannot concatenate null");

		VDMSeq result = seq();

		addAll(result, left.toArray());
		addAll(result, right.toArray());
		
		return result;
	}
	
	public static VDMSeq distConc(VDMSeq... seqs)
	{
		VDMSeq result = seq();
		
		for(VDMSeq seq : seqs)
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
	
	public static Set<Long> inds(VDMSeq seq)
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
