package org.overture.typechecker.assistant.pattern;

import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class AIgnorePatternAssistantTC
{

	//private static int var = 1; // Used in getMatchingExpression()
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public AIgnorePatternAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

//	public static PType getPossibleTypes(AIgnorePattern pattern)
//	{
//		return AstFactory.newAUnknownType(pattern.getLocation());
//	}

//	public static PExp getMatchingExpression(AIgnorePattern iptrn)
//	{
//
//		// Generate a new "any" name for use during PO generation. The name
//		// must be unique for the pattern instance.
//
//		if (iptrn.getAnyName() == null)
//		{
//			iptrn.setAnyName(new LexNameToken("", "any" + var++, iptrn.getLocation()));
//		}
//
//		return AstFactory.newAVariableExp(iptrn.getAnyName());
//	}

//	public static boolean isSimple(AIgnorePattern p)
//	{
//		return false;
//	}

}
