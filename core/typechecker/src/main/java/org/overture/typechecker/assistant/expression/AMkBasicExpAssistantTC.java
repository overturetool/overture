package org.overture.typechecker.assistant.expression;

import org.overture.ast.expressions.AMkBasicExp;
import org.overture.ast.lex.LexNameList;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class AMkBasicExpAssistantTC {
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public AMkBasicExpAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}
	public static LexNameList getOldNames(AMkBasicExp expression) {
		return PExpAssistantTC.getOldNames(expression.getArg());
	}

}
