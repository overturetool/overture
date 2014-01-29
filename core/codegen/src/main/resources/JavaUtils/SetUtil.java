
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
}
