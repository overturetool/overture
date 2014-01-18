package org.overture.codegen.ooast;

public class OoAstOperatorInfo
{
	private int precedence;
	public String mapping;
		
	public OoAstOperatorInfo(int precedenceLevel, String mapping)
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
