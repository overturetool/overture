package org.overture.ast.expressions.assistants;

import org.overture.ast.expressions.ANewExp;
import org.overturetool.vdmj.lex.LexNameList;

public class ANewExpAssistantTC {

	public static LexNameList getOldNames(ANewExp expression) {
		return PExpAssistantTC.getOldNames(expression.getArgs());
	}

}
