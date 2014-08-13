package org.overture.typechecker.util;

import java.io.Serializable;

import org.overture.ast.intf.lex.ILexNameToken;

class LexNameTokenWrapper implements Serializable
{

	/**
		 * 
		 */
	private static final long serialVersionUID = -5420007432629328108L;
	public ILexNameToken token;

	public LexNameTokenWrapper(ILexNameToken token)
	{
		this.token = token;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof LexNameTokenWrapper)
		{
			return HackLexNameToken.isEqual(this.token, ((LexNameTokenWrapper) obj).token);
		}

		return super.equals(obj);
	}

	@Override
	public int hashCode()
	{
		return this.token.hashCode();
	}

	@Override
	public String toString()
	{
		return token.toString();
	}
}