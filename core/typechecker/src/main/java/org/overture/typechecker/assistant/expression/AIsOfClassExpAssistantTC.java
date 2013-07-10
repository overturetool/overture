package org.overture.typechecker.assistant.expression;

import org.overture.ast.expressions.AIsOfClassExp;
import org.overture.ast.lex.LexNameList;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class AIsOfClassExpAssistantTC {
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public AIsOfClassExpAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}
	public static LexNameList getOldNames(AIsOfClassExp expression) {
		return PExpAssistantTC.getOldNames(expression.getExp());
	}

}
