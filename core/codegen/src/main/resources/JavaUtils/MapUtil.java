import java.util.HashMap;
import java.util.Map;

public class MapUtil
{
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
}
