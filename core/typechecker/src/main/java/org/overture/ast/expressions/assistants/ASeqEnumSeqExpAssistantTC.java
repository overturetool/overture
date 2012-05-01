package org.overture.ast.expressions.assistants;

import org.overture.ast.expressions.ASeqEnumSeqExp;
import org.overturetool.vdmj.lex.LexNameList;

public class ASeqEnumSeqExpAssistantTC {

	public static LexNameList getOldNames(ASeqEnumSeqExp expression) {
		return PExpAssistantTC.getOldNames(expression.getMembers());
	}

}
