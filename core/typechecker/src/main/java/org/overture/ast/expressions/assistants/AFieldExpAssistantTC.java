package org.overture.ast.expressions.assistants;

import org.overture.ast.expressions.AFieldExp;
import org.overturetool.vdmj.lex.LexNameList;

public class AFieldExpAssistantTC {

	public static LexNameList getOldNames(AFieldExp expression) {
		return PExpAssistantTC.getOldNames(expression.getObject());
	}

}
