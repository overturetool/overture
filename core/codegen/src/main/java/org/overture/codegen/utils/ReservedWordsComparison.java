package org.overture.codegen.utils;

import org.overture.ast.intf.lex.ILexNameToken;

public class ReservedWordsComparison extends NamingComparison
{
	public ReservedWordsComparison(String[] names)
	{
		super(names);
	}

	@Override
	public boolean isInvalid(ILexNameToken nameToken)
	{
		return this.getNames().contains(nameToken.getName());
	}

}
