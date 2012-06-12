package org.overture.typechecker.assistant.expression;

import org.overture.ast.expressions.ASetEnumSetExp;
import org.overture.ast.lex.LexNameList;

public class ASetEnumSetExpAssistantTC {

	public static LexNameList getOldNames(ASetEnumSetExp expression) {
		return PExpAssistantTC.getOldNames(expression.getMembers());
	}

}
