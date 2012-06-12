package org.overture.typechecker.assistant.expression;

import org.overture.ast.expressions.AElementsUnaryExp;
import org.overture.ast.lex.LexNameList;

public class AElementsUnaryExpAssistantTC {

	public static LexNameList getOldNames(AElementsUnaryExp expression) {
		return PExpAssistantTC.getOldNames(expression.getExp());
	}

}
