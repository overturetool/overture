package org.overture.ast.expressions.assistants;

import org.overture.ast.expressions.ATupleExp;
import org.overturetool.vdmj.lex.LexNameList;

public class ATupleExpAssistantTC {

	public static LexNameList getOldNames(ATupleExp expression) {
		return PExpAssistantTC.getOldNames(expression.getArgs());
	}

}
