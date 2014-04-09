package org.overture.codegen.utils;

import org.overture.ast.intf.lex.ILexNameToken;

public class LexNameTokenWrapper
{
	private ILexNameToken name;
	
	public LexNameTokenWrapper(ILexNameToken name)
	{
		this.name = name;
	}
	
	public ILexNameToken getName()
	{
		return name;
	}
	
	@Override
	public String toString()
	{
		return name.getName();
	}
	
	@Override
	public int hashCode()
	{		
		return 0;
		//This ensures that equals are being
		//used when instances of this class are
		//stored in collections
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if(!(obj instanceof LexNameTokenWrapper))
			return false;
		
		LexNameTokenWrapper other = (LexNameTokenWrapper) obj;
		
		return name.getName().equals(other.getName().getName());
	}
}