package org.overture.typechecker.assistant.pattern;

import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class ARealPatternAssistantTC
{
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public ARealPatternAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

//	public static PType getPossibleTypes(ARealPattern pattern)
//	{
//		return AstFactory.newARealNumericBasicType(pattern.getLocation());
//	}

//	public static PExp getMatchingExpression(ARealPattern rp)
//	{
//		ILexRealToken v = rp.getValue();
//		return AstFactory.newARealLiteralExp((LexRealToken) v.clone());
//	}

}
