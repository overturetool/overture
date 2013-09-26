package org.overture.typechecker.assistant.expression;

import org.overture.ast.expressions.AFieldNumberExp;
import org.overture.ast.lex.LexNameList;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class AFieldNumberExpAssistantTC {
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public AFieldNumberExpAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}
	public static LexNameList getOldNames(AFieldNumberExp expression) {
		return PExpAssistantTC.getOldNames(expression.getTuple());
	}

}
