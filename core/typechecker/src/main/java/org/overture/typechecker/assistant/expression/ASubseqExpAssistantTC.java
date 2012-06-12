package org.overture.typechecker.assistant.expression;

import org.overture.ast.expressions.ASubseqExp;
import org.overture.ast.lex.LexNameList;

public class ASubseqExpAssistantTC {

	public static LexNameList getOldNames(ASubseqExp expression) {
		LexNameList list = PExpAssistantTC.getOldNames(expression.getSeq());
		list.addAll(PExpAssistantTC.getOldNames(expression.getFrom()));
		list.addAll(PExpAssistantTC.getOldNames(expression.getTo()));
		return list;
	}

}
