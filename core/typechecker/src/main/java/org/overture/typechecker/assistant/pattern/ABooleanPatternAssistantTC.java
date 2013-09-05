package org.overture.typechecker.assistant.pattern;

import org.overture.ast.expressions.ABooleanConstExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.intf.lex.ILexBooleanToken;
import org.overture.ast.lex.LexBooleanToken;
import org.overture.ast.patterns.ABooleanPattern;
import org.overture.ast.types.PType;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class ABooleanPatternAssistantTC
{
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public ABooleanPatternAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	public static PType getPossibleType(ABooleanPattern pattern)
	{
		return AstFactory.newABooleanBasicType(pattern.getLocation());
	}

	public static PExp getMatchingExpression(ABooleanPattern bp)
	{
		ILexBooleanToken tok = bp.getValue();
		ABooleanConstExp res = AstFactory.newABooleanConstExp((LexBooleanToken) tok.clone());
		return res;
	}

}
