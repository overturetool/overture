package org.overture.codegen.runtime;

public class SetUtil
{
	public static VDMSet set()
	{
		return new VDMSet();
	}
	
	@SuppressWarnings("unchecked")
	public static VDMSet set(Object... elements)
	{
		if(elements == null)
			throw new IllegalArgumentException("Cannot instantiate set from null");
		
		VDMSet set = set();
		
		for(Object element : elements)
			set.add(element);
		
		return set;
	}
	
	@SuppressWarnings("unchecked")
	public static VDMSet union(VDMSet left, VDMSet right)
	{
		if(left == null || right == null)
			throw new IllegalArgumentException("Cannot union null");

		VDMSet result = new VDMSet();
		
		result.addAll(left);
		result.addAll(right);
		
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static VDMSet dunion(VDMSet sets)
	{
		if(sets == null)
			throw new IllegalArgumentException("Distributed union of null is undefined");
	
		VDMSet result = set();
		
		for(Object set : sets)
		{
			if(!(set instanceof VDMSet))
				throw new IllegalArgumentException("Distributed union only supports sets");
			
			VDMSet vdmSet = (VDMSet) set;
			result.addAll(vdmSet);
		}
		
		return result;
	}

	@SuppressWarnings("unchecked")
	public static VDMSet dinter(VDMSet sets)
	{
		if(sets == null)
			throw new IllegalArgumentException("Distributed intersection of null is undefined");
	
		VDMSet result = dunion(sets);
		
		for(Object set : sets)
		{
			if(!(set instanceof VDMSet))
				throw new IllegalArgumentException("Distributed intersection only supports sets");
			
			VDMSet vdmSet = (VDMSet) set;
			result.retainAll(vdmSet);
		}
		
		return result;
	}

	
	@SuppressWarnings("unchecked")
	public static VDMSet diff(VDMSet left, VDMSet right)
	{
		if(left == null || right == null)
			throw new IllegalArgumentException("Cannot get set difference of null");

		VDMSet result = new VDMSet();
		
		result.addAll(left);
		result.removeAll(right);
		
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static boolean psubset(VDMSet left, VDMSet right)
	{
		if(left == null || right == null)
			throw new IllegalArgumentException("proper subset is undefined for null");

		return left.size() < right.size() && right.containsAll(left);
	}
	
	@SuppressWarnings("unchecked")
	public static VDMSet intersect(VDMSet left, VDMSet right)
	{
		if(left == null || right == null)
			throw new IllegalArgumentException("Cannot intersect null");

		VDMSet result = new VDMSet();
		
		result.addAll(left);
		result.retainAll(right);
		
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static VDMSet powerset(VDMSet originalSet) {
		
		if(originalSet == null)
			throw new IllegalArgumentException("Powerset is undefined for null");
		
		VDMSet sets = SetUtil.set();
		
	    if (originalSet.isEmpty()) {
	    	sets.add(SetUtil.set());
	    	return sets;
	    }
	    
	    VDMSeq seq = SeqUtil.seq();
	    seq.addAll(originalSet);
	    
	    Object firstElement = seq.get(0);
	    VDMSet rest = SetUtil.set();
	    rest.addAll(seq.subList(1, seq.size()));
	    
	    VDMSet powerSets = powerset(rest);
	    Object[] powerSetsArray = powerSets.toArray();
	    
	    for(int i = 0; i < powerSets.size(); i++)
	    {
	    	Object obj = powerSetsArray[i];
	    	if(!(obj instanceof VDMSet))
	    		throw new IllegalArgumentException("Powerset operation is only applicable to sets. Got: " + obj);
	    	
	    	VDMSet set = (VDMSet) obj;
	    	
	    	VDMSet newSet = SetUtil.set();
	    	newSet.add(firstElement);
	    	newSet.addAll(set);
	    	sets.add(newSet);
	    	sets.add(set);
	    }
	    
	    return sets;
	}
	
	@SuppressWarnings("unchecked")
	public static VDMSet range(double first, double last)
	{
		long from = (long) Math.ceil(first);
		long to = (long) Math.floor(last);
		
		VDMSet result = new VDMSet();
		
		for (long i = from; i <= to; i++)
		{
			result.add(i);
		}
		
		return result;
	}
	
	public static boolean equals(VDMSet left, VDMSet right)
	{
		if(left == null || right == null)
			throw new IllegalArgumentException("A set cannot be compared to null");
		
		return left.equals(right);
	}
}
