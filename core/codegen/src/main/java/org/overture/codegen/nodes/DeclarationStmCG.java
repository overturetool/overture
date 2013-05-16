package org.overture.codegen.nodes;

public class DeclarationStmCG implements IStatementCG
{
	private String type;
	private String name;
	private String exp;

	public DeclarationStmCG(String type, String name, String exp)
	{
		this.type = type;
		this.name = name;
		this.exp = exp;
	}
	
	public String generate()
	{
		return type + " " + name + " = " +  exp + ";";
	}

}
