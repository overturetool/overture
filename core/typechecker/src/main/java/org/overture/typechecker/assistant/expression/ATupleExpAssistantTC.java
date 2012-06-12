package org.overture.typechecker.assistant.expression;

import org.overture.ast.expressions.ATupleExp;
import org.overture.ast.lex.LexNameList;

public class ATupleExpAssistantTC {

	public static LexNameList getOldNames(ATupleExp expression) {
		return PExpAssistantTC.getOldNames(expression.getArgs());
	}

}
