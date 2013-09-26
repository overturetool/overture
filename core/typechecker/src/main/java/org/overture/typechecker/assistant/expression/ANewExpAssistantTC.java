package org.overture.typechecker.assistant.expression;

import org.overture.ast.expressions.ANewExp;
import org.overture.ast.lex.LexNameList;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class ANewExpAssistantTC {
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public ANewExpAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}
	public static LexNameList getOldNames(ANewExp expression) {
		return PExpAssistantTC.getOldNames(expression.getArgs());
	}

}
