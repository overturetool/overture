package org.overture.ast.expressions.assistants;

import org.overture.ast.expressions.AFieldNumberExp;
import org.overturetool.vdmj.lex.LexNameList;

public class AFieldNumberExpAssistantTC {

	public static LexNameList getOldNames(AFieldNumberExp expression) {
		return PExpAssistantTC.getOldNames(expression.getTuple());
	}

}
