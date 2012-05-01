package org.overture.ast.expressions.assistants;

import org.overture.ast.expressions.ASeqCompSeqExp;
import org.overture.ast.patterns.assistants.ASetBindAssistantTC;
import org.overturetool.vdmj.lex.LexNameList;

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
