package org.overture.typechecker.assistant.expression;

import org.overture.ast.expressions.ACaseAlternative;
import org.overture.ast.expressions.ACasesExp;
import org.overture.ast.lex.LexNameList;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class ACasesExpAssistantTC {
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public ACasesExpAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}
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
