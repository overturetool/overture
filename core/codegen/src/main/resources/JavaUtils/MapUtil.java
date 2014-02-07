
public class MapUtil
{
	public static VDMMap map()
	{
		return new VDMMap();
	}
	
	public static VDMMap map(Maplet... elements)
	{
		if(elements == null)
			throw new IllegalArgumentException("Cannot instantiate map from null");
		
		return putAll(map(), elements);
	}
	
	public static VDMMap putAll(VDMMap to, Maplet... from)
	{
		if(to == null || from == null)
			throw new IllegalArgumentException("Arguments to putAll cannot be null");

		for (Maplet maplet : from)
		{
			Object left = maplet.getLeft();
			Object right = maplet.getRight();
			
			to.put(left ,right);
		}
		
		return to;
	}
	
	public static VDMSet dom(VDMMap map)
	{
		VDMSet set = SetUtil.set();
		set.addAll(map.entrySet());
			
		return set;
	}
	
	public static VDMSet rng(VDMMap map)
	{
		VDMSet set = SetUtil.set();
		set.addAll(map.values());
		
		return set;
	}
}
