package org.overture.codegen.merging;

public class TemplateCallable
{
	private String key;
	private Class<?> callable;
	
	public TemplateCallable(String key, Class<?> callable)
	{
		this.key = key;
		this.callable = callable;
	}
	
	public String getKey()
	{
		return key;
	}
	public Class<?> getCallable()
	{
		return callable;
	}
}
