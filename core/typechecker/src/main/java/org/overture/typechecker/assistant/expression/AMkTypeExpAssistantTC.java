package org.overture.typechecker.assistant.expression;

import org.overture.ast.expressions.AMkTypeExp;
import org.overture.ast.lex.LexNameList;

public class AMkTypeExpAssistantTC {

	public static LexNameList getOldNames(AMkTypeExp expression) {
		return PExpAssistantTC.getOldNames(expression.getArgs());
	}

}
