package org.overture.typechecker.assistant.pattern;

import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class AQuotePatternAssistantTC
{
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public AQuotePatternAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

//	public static PType getPossibleTypes(AQuotePattern pattern)
//	{
//		return AstFactory.newAQuoteType(((AQuotePattern) pattern).getValue().clone());
//	}

//	public static PExp getMatchingExpression(AQuotePattern qp)
//	{
//		ILexQuoteToken v = qp.getValue();
//		return AstFactory.newAQuoteLiteralExp(v.clone());
//	}

}
