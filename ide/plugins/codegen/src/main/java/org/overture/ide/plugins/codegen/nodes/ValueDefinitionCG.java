package org.overture.ide.plugins.codegen.nodes;


public class ValueDefinitionCG 
{	
	private String accessSpecifier;
	private String type;
	private String pattern;
	private String exp; 
	
	public ValueDefinitionCG(String accessSpecifier, String type, String pattern, String exp)
	{
		this.accessSpecifier = accessSpecifier;
		this.type = type;
		this.pattern = pattern;
		this.exp = exp;
	}

	public String getAccessSpecifier()
	{
		return accessSpecifier;
	}

	public String getType()
	{
		return type;
	}

	public String getPattern()
	{
		return pattern;
	}
	
	public String getExp()
	{
		return exp;
	}
	
}
