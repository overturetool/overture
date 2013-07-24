package org.overture.typechecker.assistant.expression;

import org.overture.ast.expressions.AMapletExp;
import org.overture.ast.lex.LexNameList;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class AMapletExpAssistantTC {
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public AMapletExpAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}
	public static LexNameList getOldNames(AMapletExp expression) {
		LexNameList list = PExpAssistantTC.getOldNames(expression.getLeft());
		list.addAll(PExpAssistantTC.getOldNames(expression.getRight()));
		return list;
	}

}
