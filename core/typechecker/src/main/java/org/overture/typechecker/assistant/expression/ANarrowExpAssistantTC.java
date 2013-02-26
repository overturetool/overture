package org.overture.typechecker.assistant.expression;

import org.overture.ast.expressions.ANarrowExp;
import org.overture.ast.lex.LexNameList;

public class ANarrowExpAssistantTC {

	public static LexNameList getOldNames(ANarrowExp expression) {
		return PExpAssistantTC.getOldNames(expression.getTest());
	}
	
}
