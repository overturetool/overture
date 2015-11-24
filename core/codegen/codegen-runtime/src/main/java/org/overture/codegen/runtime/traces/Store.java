package org.overture.codegen.runtime.traces;

import java.util.HashMap;
import java.util.Map;

public class Store
{
	private Map<Number, ModuleCopy> values;
	
	public Store()
	{
		this.values = new HashMap<>();
	}
	
	public void register(Number id, Object val)
	{
		values.put(id, new ObjectCopy(val));
	}
	
	public void staticReg(Number id, Class<?> clazz)
	{
		values.put(id, new ModuleCopy(clazz));
	}
	
	public Object getValue(Number id)
	{
		return values.get(id).getValue();
	}
	
	public void reset()
	{
		// TODO: Optimise this
		for(Number k : values.keySet())
		{
			values.get(k).reset();
		}
	}
}
