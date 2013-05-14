package org.overture.ide.plugins.codegen.nodes;

public class MethodDeinitionCG implements TemplateCollectionElement
{
	private String accessSpecifier;
	private String returnType;
	private String methodName;
	
	public MethodDeinitionCG(String accessSpecifier, String returnType, String methodName)
	{
		this.accessSpecifier = accessSpecifier;
		this.returnType = returnType;
		this.methodName = methodName;
	}

	public String getAccessSpecifier()
	{
		return accessSpecifier;
	}

	public String getReturnType()
	{
		return returnType;
	}

	public String getMethodName()
	{
		return methodName;
	}
	
}
