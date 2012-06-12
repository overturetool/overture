package org.overture.typechecker.assistant.expression;

import org.overture.ast.expressions.AIsExp;
import org.overture.ast.lex.LexNameList;

public class AIsExpAssistantTC {

	public static LexNameList getOldNames(AIsExp expression) {
		return PExpAssistantTC.getOldNames(expression.getTest());
	}

}
