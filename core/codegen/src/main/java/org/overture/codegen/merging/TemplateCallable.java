package org.overture.codegen.merging;

public class TemplateCallable
{
	private String key;
	private Object callable;
	
	public TemplateCallable(String key, Object callable)
	{
		this.key = key;
		this.callable = callable;
	}
	
	public String getKey()
	{
		return key;
	}
	public Object getCallable()
	{
		return callable;
	}
}
