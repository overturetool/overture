package org.overture.codegen.runtime.traces;

import java.util.HashMap;
import java.util.Map;

import org.overture.codegen.runtime.copying.DeepCopy;

public class Store
{
	private Map<Number, Pair<Object, Object>> values;
	
	public Store()
	{
		this.values = new HashMap<Number, Pair<Object, Object>>();
	}
	
	public void register(Number id, Object val)
	{
		Object deepCopy = DeepCopy.copy(val);
		values.put(id, new Pair<Object, Object>(val, deepCopy));
	}
	
	public Object getValue(Number id)
	{
		return values.get(id).getSecond();
	}
	
	public void reset()
	{
		// TODO: Optimise this
		for(Number k : values.keySet())
		{
			Pair<Object, Object> v = values.get(k);
			v.setSecond(DeepCopy.copy(v.getFirst()));
		}
	}
}
