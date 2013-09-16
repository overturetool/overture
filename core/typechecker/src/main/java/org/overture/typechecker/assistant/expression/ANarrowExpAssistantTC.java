package org.overture.typechecker.assistant.expression;

import org.overture.ast.expressions.ANarrowExp;
import org.overture.ast.lex.LexNameList;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class ANarrowExpAssistantTC {
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public ANarrowExpAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}
	public static LexNameList getOldNames(ANarrowExp expression) {
		return PExpAssistantTC.getOldNames(expression.getTest());
	}
	
}
