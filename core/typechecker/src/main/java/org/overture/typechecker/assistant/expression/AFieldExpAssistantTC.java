package org.overture.typechecker.assistant.expression;

import org.overture.ast.expressions.AFieldExp;
import org.overture.ast.lex.LexNameList;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class AFieldExpAssistantTC {
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public AFieldExpAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}
	public static LexNameList getOldNames(AFieldExp expression) {
		return PExpAssistantTC.getOldNames(expression.getObject());
	}

}
