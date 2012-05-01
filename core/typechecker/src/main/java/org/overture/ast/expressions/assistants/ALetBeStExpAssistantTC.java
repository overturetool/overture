package org.overture.ast.expressions.assistants;

import org.overture.ast.expressions.ALetBeStExp;
import org.overture.ast.patterns.assistants.PMultipleBindAssistantTC;
import org.overturetool.vdmj.lex.LexNameList;

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
