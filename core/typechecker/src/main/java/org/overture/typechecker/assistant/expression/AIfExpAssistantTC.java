package org.overture.typechecker.assistant.expression;

import org.overture.ast.expressions.AElseIfExp;
import org.overture.ast.expressions.AIfExp;
import org.overture.ast.lex.LexNameList;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class AIfExpAssistantTC {
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public AIfExpAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}
	public static LexNameList getOldNames(AIfExp expression) {
		
		LexNameList list = PExpAssistantTC.getOldNames(expression.getTest());
		list.addAll(PExpAssistantTC.getOldNames(expression.getThen()));

		for (AElseIfExp elif: expression.getElseList())
		{
			list.addAll(PExpAssistantTC.getOldNames(elif));
		}

		if (expression.getElse() != null)
		{
			list.addAll(PExpAssistantTC.getOldNames(expression.getElse()));
		}

		return list;
	}

}
