package org.overture.ast.expressions.assistants;

import org.overture.ast.expressions.ALambdaExp;
import org.overturetool.vdmj.lex.LexNameList;

public class ALambdaExpAssistantTC {

	public static LexNameList getOldNames(ALambdaExp expression) {
		return PExpAssistantTC.getOldNames(expression.getExpression());
	}

}
