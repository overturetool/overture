import java.util.Collections;


public class SeqUtil
{
	public static VDMSeq seq()
	{
		return new VDMSeq();
	}
	
	public static VDMSeq seq(Object... elements)
	{
		if(elements == null)
			throw new IllegalArgumentException("Cannot instantiate sequence from null");
		
		VDMSeq seq = seq();
		CollectionUtil.addAll(seq, elements);

		return seq;
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
	
	public static VDMSet elems(VDMSeq seq)
	{
		if(seq == null)
			throw new IllegalArgumentException("Cannot get elems of null");

		VDMSet elems = SetUtil.set();
		elems.addAll(seq);
		
		return elems;
	}
	
	public static VDMSeq reverse(VDMSeq seq)
	{
		if(seq == null)
			throw new IllegalArgumentException("Cannot reverse null");
		
		VDMSeq result = seq();
		
		CollectionUtil.addAll(result, seq.toArray());
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
	
	public static VDMSet inds(VDMSeq seq)
	{
		if(seq == null)
			throw new IllegalArgumentException("Cannot get indices of null");
		
		VDMSet indices = SetUtil.set();
		
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

		CollectionUtil.addAll(result, left.toArray());
		CollectionUtil.addAll(result, right.toArray());
		
		return result;
	}
	
	public static VDMSeq distConc(VDMSeq... seqs)
	{
		VDMSeq result = seq();
		
		for(VDMSeq seq : seqs)
		{
			CollectionUtil.addAll(result, seq.toArray());
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
}
