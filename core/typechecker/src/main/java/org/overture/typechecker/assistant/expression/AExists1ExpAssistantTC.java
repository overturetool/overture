package org.overture.typechecker.assistant.expression;

import org.overture.ast.expressions.AExists1Exp;
import org.overture.ast.lex.LexNameList;
import org.overture.typechecker.assistant.pattern.PBindAssistantTC;

public class AExists1ExpAssistantTC {

	public static LexNameList getOldNames(AExists1Exp expression) {
		LexNameList list = PBindAssistantTC.getOldNames(expression.getBind());
		list.addAll(PExpAssistantTC.getOldNames(expression.getPredicate()));
		return list;
	}

}
