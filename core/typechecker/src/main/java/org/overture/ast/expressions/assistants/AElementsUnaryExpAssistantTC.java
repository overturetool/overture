package org.overture.ast.expressions.assistants;

import org.overture.ast.expressions.AElementsUnaryExp;
import org.overturetool.vdmj.lex.LexNameList;

public class AElementsUnaryExpAssistantTC {

	public static LexNameList getOldNames(AElementsUnaryExp expression) {
		return PExpAssistantTC.getOldNames(expression.getExp());
	}

}
