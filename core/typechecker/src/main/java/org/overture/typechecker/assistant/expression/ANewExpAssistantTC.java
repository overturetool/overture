package org.overture.typechecker.assistant.expression;

import org.overture.ast.expressions.ANewExp;
import org.overture.ast.lex.LexNameList;

public class ANewExpAssistantTC {

	public static LexNameList getOldNames(ANewExp expression) {
		return PExpAssistantTC.getOldNames(expression.getArgs());
	}

}
