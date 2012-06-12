package org.overture.typechecker.assistant.expression;

import org.overture.ast.expressions.AMkBasicExp;
import org.overture.ast.lex.LexNameList;

public class AMkBasicExpAssistantTC {

	public static LexNameList getOldNames(AMkBasicExp expression) {
		return PExpAssistantTC.getOldNames(expression.getArg());
	}

}
