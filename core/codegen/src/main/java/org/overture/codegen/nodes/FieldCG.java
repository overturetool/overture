package org.overture.codegen.nodes;


public class FieldCG implements TemplateCollectionElement
{	
	private String accessSpecifier;
	private String type;
	//static
	//final
	//osv
	private String pattern;
	private String exp; 
	
	public FieldCG(String accessSpecifier, String type, String pattern, String exp)
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
