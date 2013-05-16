package org.overture.codegen.nodes;

public class ReturnStatementCG implements IStatementCG
{
	private String exp;

	public ReturnStatementCG(String exp)
	{
		super();
		this.exp = exp;
	}

	public String getExp()
	{
		return exp;
	}

	@Override
	public String generate()
	{
		return "return " + exp + ";";
	}

	
}
