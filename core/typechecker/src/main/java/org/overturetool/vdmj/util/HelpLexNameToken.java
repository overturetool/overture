package org.overturetool.vdmj.util;

import org.overture.typecheck.TypeComparator;
import org.overturetool.vdmj.lex.LexNameToken;

public class HelpLexNameToken {

	public static boolean isEqual(LexNameToken one, Object other)
	{
		if (!(other instanceof LexNameToken))
		{
			return false;
		}

		LexNameToken lother = (LexNameToken)other;

		if (one.typeQualifier != null && lother.getTypeQualifier() != null)
		{
			if (!TypeComparator.compatible(one.typeQualifier, lother.typeQualifier))
			{
				return false;
			}
		}
		else if ((one.typeQualifier != null && lother.getTypeQualifier() == null) ||
				 (one.typeQualifier == null && lother.getTypeQualifier() != null))
		{
			return false;
		}

		return one.matches(lother);
		
	}
	
}
