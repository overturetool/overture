package org.overture.typechecker.assistant.expression;

import org.overture.ast.expressions.AMkTypeExp;
import org.overture.ast.lex.LexNameList;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class AMkTypeExpAssistantTC {
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public AMkTypeExpAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}
	public static LexNameList getOldNames(AMkTypeExp expression) {
		return PExpAssistantTC.getOldNames(expression.getArgs());
	}

}
