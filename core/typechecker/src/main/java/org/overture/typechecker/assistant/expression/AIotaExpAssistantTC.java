package org.overture.typechecker.assistant.expression;

import org.overture.ast.expressions.AIotaExp;
import org.overture.ast.lex.LexNameList;
import org.overture.typechecker.assistant.pattern.PBindAssistantTC;

public class AIotaExpAssistantTC {

	public static LexNameList getOldNames(AIotaExp expression) {
		LexNameList list = PBindAssistantTC.getOldNames(expression.getBind());
		list.addAll(PExpAssistantTC.getOldNames(expression.getPredicate()));
		return list;
	}

}
