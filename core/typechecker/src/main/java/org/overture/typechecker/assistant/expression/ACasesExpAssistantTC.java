package org.overture.typechecker.assistant.expression;

import org.overture.ast.expressions.ACaseAlternative;
import org.overture.ast.expressions.ACasesExp;
import org.overture.ast.lex.LexNameList;

public class ACasesExpAssistantTC {

	public static LexNameList getOldNames(ACasesExp expression) {
		LexNameList list = PExpAssistantTC.getOldNames(expression.getExpression());

		for (ACaseAlternative c: expression.getCases())
		{
			list.addAll(ACaseAlternativeAssistantTC.getOldNames(c));
		}

		if (expression.getOthers() != null)
		{
			list.addAll(PExpAssistantTC.getOldNames(expression.getOthers()));
		}

		return list;
	}

}
