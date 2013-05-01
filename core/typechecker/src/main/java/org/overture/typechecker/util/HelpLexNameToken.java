package org.overture.typechecker.util;

import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.lex.LexNameToken;
import org.overture.typechecker.TypeComparator;

public class HelpLexNameToken {

	public static boolean isEqual(ILexNameToken one, Object other)
	{
		if (!(other instanceof ILexNameToken))
		{
			return false;
		}

		ILexNameToken lother = (ILexNameToken)other;

		if (one.getTypeQualifier() != null && lother.getTypeQualifier() != null)
		{
			if (!TypeComparator.compatible(one.getTypeQualifier(), lother.getTypeQualifier()))
			{
				return false;
			}
		}
		else if ((one.getTypeQualifier() != null && lother.getTypeQualifier() == null) ||
				 (one.getTypeQualifier() == null && lother.getTypeQualifier() != null))
		{
			return false;
		}

		return one.matches(lother);
		
	}
	
	public static boolean isEqual(Object one, Object other)
	{
		
		if(one instanceof ILexNameToken)
		{
			return isEqual((ILexNameToken)one, other);
		}
		return false;
		
	}
}
