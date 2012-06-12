package org.overture.typechecker.assistant.expression;

import org.overture.ast.expressions.AFuncInstatiationExp;
import org.overture.ast.lex.LexNameList;

public class AFuncInstatiationExpAssistantTC {

	public static LexNameList getOldNames(AFuncInstatiationExp expression) {
		return PExpAssistantTC.getOldNames(expression.getFunction());
	}

}
