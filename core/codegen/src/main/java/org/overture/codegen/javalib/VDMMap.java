package org.overture.codegen.javalib;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


@SuppressWarnings("rawtypes")
public class VDMMap extends HashMap implements ValueType
{
	private static final long serialVersionUID = -3288711341768577550L;

	@SuppressWarnings("unchecked")
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
	
	@Override
	public String toString()
	{
		Set entries = this.entrySet(); 
		Iterator iterator = entries.iterator();
		
		if(!iterator.hasNext())
			return "{|->}";
		
		StringBuilder sb = new StringBuilder();
		sb.append('{');
		
		for (;;)
		{
			Object next = iterator.next();
			
			if(!(next instanceof Map.Entry))
				continue;
			
			Map.Entry entry = (Map.Entry) next;
			
			Object key = entry.getKey();
			Object value = entry.getValue();

			sb.append(key == this ? "(this Collection)" : key);
			sb.append(" |-> ");
			sb.append(value == this ? "(this Collection)" : value);
			
			if(!iterator.hasNext())
				return sb.append('}').toString();
			
			sb.append(", ");
		}
	}
}
