package org.overture.ast.expressions.assistants;

import org.overture.ast.expressions.AIotaExp;
import org.overture.ast.patterns.assistants.PBindAssistantTC;
import org.overturetool.vdmj.lex.LexNameList;

public class AIotaExpAssistantTC {

	public static LexNameList getOldNames(AIotaExp expression) {
		LexNameList list = PBindAssistantTC.getOldNames(expression.getBind());
		list.addAll(PExpAssistantTC.getOldNames(expression.getPredicate()));
		return list;
	}

}
