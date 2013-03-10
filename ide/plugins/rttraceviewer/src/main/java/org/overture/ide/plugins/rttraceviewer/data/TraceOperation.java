package org.overture.ide.plugins.rttraceviewer.data;

public class TraceOperation {

	private String name;
	private Boolean isAsync;
	private Boolean isStatic;
	private String className;
	
	public TraceOperation(String name, Boolean isAsync, Boolean isStatic, String className)
	{
		this.name = name;
		this.isAsync = isAsync;
		this.isStatic = isStatic;
		this.className = className;
	}
	
	public String getName()
	{
		return name;
	}
	
	public Boolean getIsAsync()
	{
		return isAsync;
	}
	
	public Boolean getIsStatic()
	{
		return isStatic;
	}
	
	public String getClassName()
	{
		return className;
	}
}
