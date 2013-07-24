package org.overture.typechecker.assistant.expression;

import org.overture.ast.expressions.AIsOfBaseClassExp;
import org.overture.ast.lex.LexNameList;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class AIsOfBaseClassExpAssistantTC {
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public AIsOfBaseClassExpAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}
	public static LexNameList getOldNames(AIsOfBaseClassExp expression) {
		return PExpAssistantTC.getOldNames(expression.getExp());
	}

}
