import java.util.Set;


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

		VDMMap map = map();
		
		if(elements.length == 0)
		{
			return map;
		}
		else
		{
			Maplet firstElement = elements[0];
			map.put(firstElement.getLeft(), firstElement.getRight());
		}
		
		for (int i = 1; i < elements.length; i++)
		{
			Maplet maplet = elements[i];
			
			Object mapletKey = maplet.getLeft();
			Object mapletValue = maplet.getRight();

			if(map.containsKey(mapletKey))
			{
				Object mapValue = map.get(mapletKey);
				
				if (differentValues(mapletValue, mapValue))
					throw new IllegalArgumentException("Duplicate keys that have different values are not allowed");
			}
			
			map.put(mapletKey ,mapletValue);
		}
		
		return map;
	}
	
	private static boolean differentValues(Object leftVal, Object rightVal)
	{
		return (leftVal == null && rightVal != null)
				|| (leftVal != null && !leftVal.equals(rightVal));
	}
	
	public static VDMSet dom(VDMMap map)
	{
		if(map == null)
			throw new IllegalArgumentException("Map domain is undefined for null");
		
		VDMSet set = SetUtil.set();
		set.addAll(map.entrySet());
			
		return set;
	}
	
	public static VDMSet rng(VDMMap map)
	{
		if(map == null)
			throw new IllegalArgumentException("Map range is undefined for null");
		
		VDMSet set = SetUtil.set();
		set.addAll(map.values());
		
		return set;
	}
	
	public static VDMMap munion(VDMMap left, VDMMap right)
	{
		if(left == null || right == null)
			throw new IllegalArgumentException("Cannot munion null");
		
		VDMMap result = map();
		
		result.putAll(left);
		
		Set rightKeys = right.keySet();

		for(Object rightKey : rightKeys)
		{
			if(left.containsKey(rightKey))
			{
				Object leftVal = left.get(rightKey);
				Object rightVal = right.get(rightKey);
				
				if(differentValues(leftVal, rightVal))
					throw new IllegalAccessError("Duplicate keys that have different values are not allowed");

				result.put(rightKey, rightVal);		
			}
		}
		
		return result;
	}
	
	public static VDMMap override(VDMMap left, VDMMap right)
	{
		if(left == null || right == null)
			throw new IllegalArgumentException("Cannot override null");
		
		VDMMap result = map();
		
		result.putAll(left);
		result.putAll(right);
		
		return result;
	}
}
