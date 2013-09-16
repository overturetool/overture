package org.overture.typechecker.assistant.expression;

import org.overture.ast.expressions.ATupleExp;
import org.overture.ast.lex.LexNameList;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class ATupleExpAssistantTC {
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public ATupleExpAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}
	public static LexNameList getOldNames(ATupleExp expression) {
		return PExpAssistantTC.getOldNames(expression.getArgs());
	}

}
