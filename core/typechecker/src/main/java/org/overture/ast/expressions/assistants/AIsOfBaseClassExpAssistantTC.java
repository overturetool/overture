package org.overture.ast.expressions.assistants;

import org.overture.ast.expressions.AIsOfBaseClassExp;
import org.overturetool.vdmj.lex.LexNameList;

public class AIsOfBaseClassExpAssistantTC {

	public static LexNameList getOldNames(AIsOfBaseClassExp expression) {
		return PExpAssistantTC.getOldNames(expression.getExp());
	}

}
