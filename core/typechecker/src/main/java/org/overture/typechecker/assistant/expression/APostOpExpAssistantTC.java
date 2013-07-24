package org.overture.typechecker.assistant.expression;

import org.overture.ast.expressions.APostOpExp;
import org.overture.ast.lex.LexNameList;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class APostOpExpAssistantTC {
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public APostOpExpAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}
	public static LexNameList getOldNames(APostOpExp expression) {
		return PExpAssistantTC.getOldNames(expression.getPostexpression());
	}

}
