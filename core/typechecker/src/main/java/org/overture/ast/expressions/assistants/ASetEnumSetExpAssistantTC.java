package org.overture.ast.expressions.assistants;

import org.overture.ast.expressions.ASetEnumSetExp;
import org.overturetool.vdmj.lex.LexNameList;

public class ASetEnumSetExpAssistantTC {

	public static LexNameList getOldNames(ASetEnumSetExp expression) {
		return PExpAssistantTC.getOldNames(expression.getMembers());
	}

}
