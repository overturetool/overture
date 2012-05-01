package org.overture.ast.expressions.assistants;

import org.overture.ast.expressions.AElementsUnaryExp;
import org.overture.ast.expressions.SUnaryExp;
import org.overturetool.vdmj.lex.LexNameList;

public class SUnaryExpAssistantTC {

	public static LexNameList getOldNames(SUnaryExp expression) {
		switch (expression.kindSUnaryExp()) {		
		case ELEMENTS:
			return AElementsUnaryExpAssistantTC.getOldNames((AElementsUnaryExp) expression);
		default:
			return PExpAssistantTC.getOldNames(expression.getExp());
		}
	}

}
