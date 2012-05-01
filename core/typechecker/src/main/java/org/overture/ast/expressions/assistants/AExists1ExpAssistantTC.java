package org.overture.ast.expressions.assistants;

import org.overture.ast.expressions.AExists1Exp;
import org.overture.ast.patterns.assistants.PBindAssistantTC;
import org.overturetool.vdmj.lex.LexNameList;

public class AExists1ExpAssistantTC {

	public static LexNameList getOldNames(AExists1Exp expression) {
		LexNameList list = PBindAssistantTC.getOldNames(expression.getBind());
		list.addAll(PExpAssistantTC.getOldNames(expression.getPredicate()));
		return list;
	}

}
