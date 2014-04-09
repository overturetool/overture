package org.overture.typechecker;

import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class LexNameTokenAssistent
{
	public ITypeCheckerAssistantFactory af;
	
	public LexNameTokenAssistent(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}
	
	public boolean isEqual(ILexNameToken token, Object other)
	{
		if (!(other instanceof ILexNameToken))
		{
			return false;
		}

		ILexNameToken lother = (ILexNameToken) other;

		if (token.getTypeQualifier() != null
				&& lother.getTypeQualifier() != null)
		{
			if (!TypeComparator.compatible(token.getTypeQualifier(), lother.getTypeQualifier(), af ))
			{
				return false;
			}
		} else if (token.getTypeQualifier() != null
				&& lother.getTypeQualifier() == null
				|| token.getTypeQualifier() == null
				&& lother.getTypeQualifier() != null)
		{
			return false;
		}

		return token.matches(lother);
	}
}
