package org.overture.codegen.ir;

public class IROperatorInfo
{
	private int precedence;
	public String mapping;
		
	public IROperatorInfo(int precedenceLevel, String mapping)
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
