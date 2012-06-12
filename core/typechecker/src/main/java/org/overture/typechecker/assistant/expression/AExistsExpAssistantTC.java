package org.overture.typechecker.assistant.expression;

import org.overture.ast.expressions.AExistsExp;
import org.overture.ast.lex.LexNameList;
import org.overture.ast.patterns.PMultipleBind;
import org.overture.typechecker.assistant.pattern.PMultipleBindAssistantTC;

public class AExistsExpAssistantTC {

	public static LexNameList getOldNameS(AExistsExp expression) {
		
		LexNameList list = new LexNameList();

		for (PMultipleBind mb: expression.getBindList())
		{
			list.addAll(PMultipleBindAssistantTC.getOldNames(mb));
		}

		list.addAll(PExpAssistantTC.getOldNames(expression.getPredicate()));
		return list;
	}

}
