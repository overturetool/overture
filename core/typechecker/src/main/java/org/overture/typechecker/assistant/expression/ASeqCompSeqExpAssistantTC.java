package org.overture.typechecker.assistant.expression;

import org.overture.ast.expressions.ASeqCompSeqExp;
import org.overture.ast.lex.LexNameList;
import org.overture.typechecker.assistant.pattern.ASetBindAssistantTC;

public class ASeqCompSeqExpAssistantTC {

	public static LexNameList getOldNames(ASeqCompSeqExp expression) {
		LexNameList list = PExpAssistantTC.getOldNames(expression.getFirst());
		list.addAll(ASetBindAssistantTC.getOldNames(expression.getSetBind()));

		if (expression.getPredicate() != null) {
			list.addAll(PExpAssistantTC.getOldNames(expression.getPredicate()));
		}

		return list;
	}

}
