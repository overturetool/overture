package org.overture.codegen.utils;

import java.util.Arrays;
import java.util.List;

import org.overture.ast.intf.lex.ILexNameToken;

public abstract class NamingComparison
{
	private List<String> names;
		
	public NamingComparison(String[] names)
	{
		this.names = Arrays.asList(names);
	}
	
	public abstract boolean isInvalid(ILexNameToken nameToken);
	
	public List<String> getNames()
	{
		return this.names;
	}
}
