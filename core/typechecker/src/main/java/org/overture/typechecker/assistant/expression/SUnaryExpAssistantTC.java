package org.overture.typechecker.assistant.expression;

import org.overture.ast.expressions.AElementsUnaryExp;
import org.overture.ast.expressions.SUnaryExp;
import org.overture.ast.lex.LexNameList;

public class SUnaryExpAssistantTC {

	public static LexNameList getOldNames(SUnaryExp expression) {
		switch (expression.kindSUnaryExp()) {		
		case AElementsUnaryExp.kindSUnaryExp:
			return AElementsUnaryExpAssistantTC.getOldNames((AElementsUnaryExp) expression);
		default:
			return PExpAssistantTC.getOldNames(expression.getExp());
		}
	}

}
