package org.overture.typechecker.assistant.expression;

import org.overture.ast.expressions.AElseIfExp;
import org.overture.ast.lex.LexNameList;

public class AElseIfExpAssistantTC {

	public static LexNameList getOldNames(AElseIfExp expression) {
		LexNameList list = PExpAssistantTC.getOldNames(expression.getElseIf());
		list.addAll(PExpAssistantTC.getOldNames(expression.getThen()));
		return list;
	}

}
