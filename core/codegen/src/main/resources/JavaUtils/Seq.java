import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


public class Seq
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
	
	public static VDMSeq mod(VDMSeq seq, Maplet... maplets)
	{
		for(Maplet maplet : maplets)
		{
			Object left = maplet.getLeft();
			Object right = maplet.getRight();
			
			if(!(left instanceof Long))
				throw new IllegalArgumentException("Domain values of maplets in a sequence modification must be of type nat1");
			
			Long key = (Long) left;
			seq.set(Utils.index(key), right);
		}
		
		return seq;
	}
	
	public static VDMSeq reverse(VDMSeq seq)
	{
		if(seq == null)
			throw new IllegalArgumentException("Cannot reverse null");
		
		VDMSeq result = seq();
		
		addAll(result, seq.toArray());

		Collections.reverse(result);
		
		return result;
	}
	
	public static VDMSeq tail(VDMSeq seq)
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
	
	public static boolean equals(VDMSeq left, VDMSeq right)
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
	
	public static VDMSeq conc(VDMSeq left, VDMSeq right)
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
}
