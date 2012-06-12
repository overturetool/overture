package org.overture.typechecker.assistant.expression;

import org.overture.ast.expressions.ALambdaExp;
import org.overture.ast.lex.LexNameList;

public class ALambdaExpAssistantTC {

	public static LexNameList getOldNames(ALambdaExp expression) {
		return PExpAssistantTC.getOldNames(expression.getExpression());
	}

}
