package org.overture.typechecker;

import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.lex.LexNameToken;

public class LexNameTokenAssistent {
	public static boolean isEqual(ILexNameToken token, Object other) {
		if (!(other instanceof ILexNameToken))
		{
			return false;
		}

		ILexNameToken lother = (ILexNameToken)other;

		if (token.getTypeQualifier() != null && lother.getTypeQualifier() != null)
		{
			if (!TypeComparator.compatible(token.getTypeQualifier(), lother.getTypeQualifier()))
			{
				return false;
			}
		}
		else if ((token.getTypeQualifier() != null && lother.getTypeQualifier() == null) ||
				 (token.getTypeQualifier() == null && lother.getTypeQualifier() != null))
		{
			return false;
		}

		return token.matches(lother);	
		}
}
