package org.overture.codegen.visitor;

public enum Vdm2JavaBasicTypeMappings
{

	BOOL("boolean"),
	CHAR("char"),
	//TODO: TOKEN
	
	//NUMERIC
	INT("long"),
	NAT_ONE("int"),
	NAT("int"),
	//TODO: RATIONAL not handled
	REAL("double");
	
	
	private String keyword;
	
	Vdm2JavaBasicTypeMappings(String keyword)
	{
		this.keyword = keyword;
	}
	
	@Override
	public String toString()
	{
		return keyword;
	}
}
