package org.overture.typechecker.assistant.expression;

import org.overture.ast.expressions.AIsOfBaseClassExp;
import org.overture.ast.lex.LexNameList;

public class AIsOfBaseClassExpAssistantTC {

	public static LexNameList getOldNames(AIsOfBaseClassExp expression) {
		return PExpAssistantTC.getOldNames(expression.getExp());
	}

}
