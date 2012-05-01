package org.overture.ast.expressions.assistants;

import org.overture.ast.expressions.ASubseqExp;
import org.overturetool.vdmj.lex.LexNameList;

public class ASubseqExpAssistantTC {

	public static LexNameList getOldNames(ASubseqExp expression) {
		LexNameList list = PExpAssistantTC.getOldNames(expression.getSeq());
		list.addAll(PExpAssistantTC.getOldNames(expression.getFrom()));
		list.addAll(PExpAssistantTC.getOldNames(expression.getTo()));
		return list;
	}

}
