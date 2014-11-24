package org.overture.codegen.vdm2cpp;

public class ExtDependency {
	private String name;
	private String namespace;
	private String include;
	public ExtDependency(String name, String namespace, String include) 
	{
		// TODO Auto-generated constructor stub
		this.name = name;
		this.namespace = namespace;
		this.include = include;
	}
	
	public String getName() 
	{
		return name;
	}
	
	public String getNamespace()
	{
		return namespace;
	}
	
	public String getInclude()
	{
		return include;
	}

}
