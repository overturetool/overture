package org.overture.typechecker.assistant.expression;

import org.overture.ast.expressions.AFieldNumberExp;
import org.overture.ast.lex.LexNameList;

public class AFieldNumberExpAssistantTC {

	public static LexNameList getOldNames(AFieldNumberExp expression) {
		return PExpAssistantTC.getOldNames(expression.getTuple());
	}

}
