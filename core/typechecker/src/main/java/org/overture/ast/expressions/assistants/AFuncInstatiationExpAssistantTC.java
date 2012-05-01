package org.overture.ast.expressions.assistants;

import org.overture.ast.expressions.AFuncInstatiationExp;
import org.overturetool.vdmj.lex.LexNameList;

public class AFuncInstatiationExpAssistantTC {

	public static LexNameList getOldNames(AFuncInstatiationExp expression) {
		return PExpAssistantTC.getOldNames(expression.getFunction());
	}

}
