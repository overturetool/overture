package org.overture.typechecker.assistant.expression;

import org.overture.ast.expressions.AElementsUnaryExp;
import org.overture.ast.expressions.SUnaryExp;
import org.overture.ast.lex.LexNameList;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class SUnaryExpAssistantTC {
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public SUnaryExpAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}
	public static LexNameList getOldNames(SUnaryExp expression) {
		if (expression instanceof AElementsUnaryExp) {
			return AElementsUnaryExpAssistantTC.getOldNames((AElementsUnaryExp) expression);
		} else {
			return PExpAssistantTC.getOldNames(expression.getExp());
		}
	}

}
