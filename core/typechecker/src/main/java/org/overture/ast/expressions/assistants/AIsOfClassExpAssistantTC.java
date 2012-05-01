package org.overture.ast.expressions.assistants;

import org.overture.ast.expressions.AIsOfClassExp;
import org.overturetool.vdmj.lex.LexNameList;

public class AIsOfClassExpAssistantTC {

	public static LexNameList getOldNames(AIsOfClassExp expression) {
		return PExpAssistantTC.getOldNames(expression.getExp());
	}

}
