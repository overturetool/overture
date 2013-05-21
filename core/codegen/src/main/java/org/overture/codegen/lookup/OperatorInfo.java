package org.overture.codegen.lookup;

public class OperatorInfo
{
	private int precedence;
	public String mapping;
		
	public OperatorInfo(int precedenceLevel, String mapping)
	{
		this.precedence = precedenceLevel;
		this.mapping = mapping;
	}
	public int getPrecedence()
	{
		return precedence;
	}
	public String getMapping()
	{
		return mapping;
	}
	
}
