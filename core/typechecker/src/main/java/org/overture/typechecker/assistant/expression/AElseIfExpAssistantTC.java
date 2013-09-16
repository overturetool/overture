package org.overture.typechecker.assistant.expression;

import org.overture.ast.expressions.AElseIfExp;
import org.overture.ast.lex.LexNameList;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class AElseIfExpAssistantTC {
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public AElseIfExpAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}
	public static LexNameList getOldNames(AElseIfExp expression) {
		LexNameList list = PExpAssistantTC.getOldNames(expression.getElseIf());
		list.addAll(PExpAssistantTC.getOldNames(expression.getThen()));
		return list;
	}

}
