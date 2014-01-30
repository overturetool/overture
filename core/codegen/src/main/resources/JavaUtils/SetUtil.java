
public class SetUtil
{
	public static VDMSet set()
	{
		return new VDMSet();
	}
	
	public static VDMSet set(Object... elements)
	{
		if(elements == null)
			throw new IllegalArgumentException("Cannot instantiate set from null");
		
		VDMSet set = set();
		CollectionUtil.addAll(set(), elements);
		
		return set;
	}
	
	public static boolean equals(VDMSet left, VDMSet right)
	{
		if(left == null || right == null)
			throw new IllegalArgumentException("A set cannot be compared to null");
		
		if(left.size() != right.size())
			return false;
		
		return left.containsAll(right);
	}
}
