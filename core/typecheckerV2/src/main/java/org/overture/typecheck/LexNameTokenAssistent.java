package org.overture.typecheck;

import org.overturetool.vdmjV2.lex.LexNameToken;

public class LexNameTokenAssistent {
	public static boolean isEqual(LexNameToken token, Object other) {
		if (!(other instanceof LexNameToken))
		{
			return false;
		}

		LexNameToken lother = (LexNameToken)other;

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
