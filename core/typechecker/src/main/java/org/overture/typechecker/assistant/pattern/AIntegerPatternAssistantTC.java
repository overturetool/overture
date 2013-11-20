package org.overture.typechecker.assistant.pattern;

import org.overture.ast.expressions.PExp;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.lex.LexIntegerToken;
import org.overture.ast.patterns.AIntegerPattern;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class AIntegerPatternAssistantTC
{
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public AIntegerPatternAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

//	public static PType getPossibleTypes(AIntegerPattern pattern)
//	{
//		return SNumericBasicTypeAssistantTC.typeOf(pattern.getValue().getValue(), pattern.getLocation());
//	}

	public static PExp getMatchingExpression(AIntegerPattern intptrn)
	{
		return AstFactory.newAIntLiteralExp((LexIntegerToken) intptrn.getValue().clone());
	}

}
