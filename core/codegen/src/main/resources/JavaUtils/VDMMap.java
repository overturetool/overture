import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class VDMMap extends HashMap implements ValueType
{
	public VDMMap clone()
	{
		VDMMap mapClone = new VDMMap();
		
		Iterator iterator = this.entrySet().iterator();
		
		while(iterator.hasNext())
		{
			Map.Entry entry = (Map.Entry) iterator.next();
			
			Object key = entry.getKey();
			Object value = entry.getValue();
			
			if(key instanceof ValueType)
				key = ((ValueType) key).clone();
			
			if(value instanceof ValueType)
				value = ((ValueType) value).clone();
			
			mapClone.put(key, value);
		}
		
		return mapClone;
	}
}
