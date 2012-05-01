package org.overture.ast.expressions.assistants;

import org.overture.ast.expressions.AMkTypeExp;
import org.overturetool.vdmj.lex.LexNameList;

public class AMkTypeExpAssistantTC {

	public static LexNameList getOldNames(AMkTypeExp expression) {
		return PExpAssistantTC.getOldNames(expression.getArgs());
	}

}
