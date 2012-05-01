package org.overture.ast.expressions.assistants;

import org.overture.ast.expressions.AIsExp;
import org.overturetool.vdmj.lex.LexNameList;

public class AIsExpAssistantTC {

	public static LexNameList getOldNames(AIsExp expression) {
		return PExpAssistantTC.getOldNames(expression.getTest());
	}

}
