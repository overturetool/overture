package org.overture.typechecker.assistant.expression;

import org.overture.ast.expressions.ALetBeStExp;
import org.overture.ast.lex.LexNameList;
import org.overture.typechecker.assistant.pattern.PMultipleBindAssistantTC;

public class ALetBeStExpAssistantTC {

	public static LexNameList getOldNames(ALetBeStExp expression) {
		LexNameList list = PMultipleBindAssistantTC.getOldNames(expression.getBind());

		if (expression.getSuchThat() != null)
		{
			list.addAll(PExpAssistantTC.getOldNames(expression.getSuchThat()));
		}

		list.addAll(PExpAssistantTC.getOldNames(expression.getValue()));
		return list;
	}

}
