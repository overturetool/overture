package org.overture.typechecker.assistant.pattern;

import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class ABooleanPatternAssistantTC
{
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public ABooleanPatternAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

//	public static PType getPossibleType(ABooleanPattern pattern)
//	{
//		return AstFactory.newABooleanBasicType(pattern.getLocation());
//	}

//	public static PExp getMatchingExpression(ABooleanPattern bp)
//	{
//		ILexBooleanToken tok = bp.getValue();
//		ABooleanConstExp res = AstFactory.newABooleanConstExp((LexBooleanToken) tok.clone());
//		return res;
//	}

}
