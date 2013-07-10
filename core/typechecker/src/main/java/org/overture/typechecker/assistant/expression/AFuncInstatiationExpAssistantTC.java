package org.overture.typechecker.assistant.expression;

import org.overture.ast.expressions.AFuncInstatiationExp;
import org.overture.ast.lex.LexNameList;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class AFuncInstatiationExpAssistantTC {
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public AFuncInstatiationExpAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}
	public static LexNameList getOldNames(AFuncInstatiationExp expression) {
		return PExpAssistantTC.getOldNames(expression.getFunction());
	}

}
