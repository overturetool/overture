package org.overture.typechecker.assistant.expression;

import org.overture.ast.expressions.AFieldExp;
import org.overture.ast.lex.LexNameList;

public class AFieldExpAssistantTC {

	public static LexNameList getOldNames(AFieldExp expression) {
		return PExpAssistantTC.getOldNames(expression.getObject());
	}

}
