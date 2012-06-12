package org.overture.typechecker.assistant.expression;

import org.overture.ast.expressions.AMapletExp;
import org.overture.ast.lex.LexNameList;

public class AMapletExpAssistantTC {

	public static LexNameList getOldNames(AMapletExp expression) {
		LexNameList list = PExpAssistantTC.getOldNames(expression.getLeft());
		list.addAll(PExpAssistantTC.getOldNames(expression.getRight()));
		return list;
	}

}
