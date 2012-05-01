package org.overture.ast.expressions.assistants;

import org.overture.ast.expressions.AVariableExp;
import org.overturetool.vdmj.lex.LexNameList;

public class AVariableExpAssistantTC {

	public static LexNameList getOldNames(AVariableExp expression) {
		if (expression.getName().old)
		{
			return new LexNameList(expression.getName());
		}
		else
		{
			return new LexNameList();
		}
	}

}
