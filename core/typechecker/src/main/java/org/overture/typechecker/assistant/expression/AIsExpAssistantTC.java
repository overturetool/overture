package org.overture.typechecker.assistant.expression;

import org.overture.ast.expressions.AIsExp;
import org.overture.ast.lex.LexNameList;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class AIsExpAssistantTC {
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public AIsExpAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}
	public static LexNameList getOldNames(AIsExp expression) {
		return PExpAssistantTC.getOldNames(expression.getTest());
	}

}
