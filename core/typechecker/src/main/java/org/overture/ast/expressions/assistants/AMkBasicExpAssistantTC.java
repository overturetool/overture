package org.overture.ast.expressions.assistants;

import org.overture.ast.expressions.AMkBasicExp;
import org.overturetool.vdmj.lex.LexNameList;

public class AMkBasicExpAssistantTC {

	public static LexNameList getOldNames(AMkBasicExp expression) {
		return PExpAssistantTC.getOldNames(expression.getArg());
	}

}
