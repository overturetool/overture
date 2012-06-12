package org.overture.typechecker.assistant.expression;

import org.overture.ast.expressions.AForAllExp;
import org.overture.ast.lex.LexNameList;
import org.overture.ast.patterns.PMultipleBind;
import org.overture.typechecker.assistant.pattern.PMultipleBindAssistantTC;

public class AForAllExpAssistantTC {

	public static LexNameList getOldNames(AForAllExp expression) {
		LexNameList list = new LexNameList();

		for (PMultipleBind mb: expression.getBindList())
		{
			list.addAll(PMultipleBindAssistantTC.getOldNames(mb));
		}

		list.addAll(PExpAssistantTC.getOldNames(expression.getPredicate()));
		return list;
	}

}
