package org.overture.typechecker;

import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class LexNameTokenAssistant
{
	public ITypeCheckerAssistantFactory af;
	
	public LexNameTokenAssistant(ITypeCheckerAssistantFactory af)
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
			if (!af.getTypeComparator().compatible(token.getTypeQualifier(), lother.getTypeQualifier() ))
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
