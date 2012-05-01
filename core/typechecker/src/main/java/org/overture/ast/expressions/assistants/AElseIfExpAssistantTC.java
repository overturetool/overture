package org.overture.ast.expressions.assistants;

import org.overture.ast.expressions.AElseIfExp;
import org.overturetool.vdmj.lex.LexNameList;

public class AElseIfExpAssistantTC {

	public static LexNameList getOldNames(AElseIfExp expression) {
		LexNameList list = PExpAssistantTC.getOldNames(expression.getElseIf());
		list.addAll(PExpAssistantTC.getOldNames(expression.getThen()));
		return list;
	}

}
