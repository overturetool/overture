package org.overture.typechecker.assistant.expression;

import org.overture.ast.expressions.AElementsUnaryExp;
import org.overture.ast.lex.LexNameList;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class AElementsUnaryExpAssistantTC {
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public AElementsUnaryExpAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}
	public static LexNameList getOldNames(AElementsUnaryExp expression) {
		return PExpAssistantTC.getOldNames(expression.getExp());
	}

}
