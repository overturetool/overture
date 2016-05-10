package org.overture.codegen.runtime.traces;

import java.util.HashMap;
import java.util.Map;

import org.overture.codegen.runtime.Utils;

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
		for(Map.Entry<Number, ModuleCopy> entry : values.entrySet())
		{
			entry.getValue().reset();
		}
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		
		String sep = "";
		
		for(Map.Entry<Number, ModuleCopy> entry : values.entrySet())
		{
			sb.append(sep);
			sb.append(Utils.toString(entry.getKey()));
			sb.append(" |-> ");
			sb.append(Utils.toString(entry.getValue()));
			sep = ", ";
		}
		
		sb.append("}");
		
		return sb.toString();
	}
}
