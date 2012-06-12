package org.overture.typechecker.assistant.expression;

import org.overture.ast.expressions.ASeqEnumSeqExp;
import org.overture.ast.lex.LexNameList;

public class ASeqEnumSeqExpAssistantTC {

	public static LexNameList getOldNames(ASeqEnumSeqExp expression) {
		return PExpAssistantTC.getOldNames(expression.getMembers());
	}

}
