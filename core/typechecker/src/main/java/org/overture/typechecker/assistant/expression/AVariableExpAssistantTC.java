package org.overture.typechecker.assistant.expression;

import org.overture.ast.expressions.AVariableExp;
import org.overture.ast.lex.LexNameList;

public class AVariableExpAssistantTC {

	public static LexNameList getOldNames(AVariableExp expression) {
		if (expression.getName().getOld())
		{
			return new LexNameList(expression.getName());
		}
		else
		{
			return new LexNameList();
		}
	}

}
