package org.overture.typechecker.assistant.expression;

import org.overture.ast.expressions.AIsOfClassExp;
import org.overture.ast.lex.LexNameList;

public class AIsOfClassExpAssistantTC {

	public static LexNameList getOldNames(AIsOfClassExp expression) {
		return PExpAssistantTC.getOldNames(expression.getExp());
	}

}
