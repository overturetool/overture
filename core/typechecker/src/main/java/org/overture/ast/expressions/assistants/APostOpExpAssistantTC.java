package org.overture.ast.expressions.assistants;

import org.overture.ast.expressions.APostOpExp;
import org.overturetool.vdmj.lex.LexNameList;

public class APostOpExpAssistantTC {

	public static LexNameList getOldNames(APostOpExp expression) {
		return PExpAssistantTC.getOldNames(expression.getPostexpression());
	}

}
