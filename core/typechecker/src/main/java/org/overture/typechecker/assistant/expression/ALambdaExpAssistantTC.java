package org.overture.typechecker.assistant.expression;

import org.overture.ast.expressions.ALambdaExp;
import org.overture.ast.lex.LexNameList;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class ALambdaExpAssistantTC {
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public ALambdaExpAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}
	public static LexNameList getOldNames(ALambdaExp expression) {
		return PExpAssistantTC.getOldNames(expression.getExpression());
	}

}
