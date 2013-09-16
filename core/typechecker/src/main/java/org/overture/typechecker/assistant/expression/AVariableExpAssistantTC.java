package org.overture.typechecker.assistant.expression;

import org.overture.ast.expressions.AVariableExp;
import org.overture.ast.lex.LexNameList;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class AVariableExpAssistantTC {
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public AVariableExpAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}
	public static LexNameList getOldNames(AVariableExp expression) {
		if (expression.getName().getOld())
		{
			return new LexNameList(expression.getName());
		}
		else
		{
			return new LexNameList();
		}
	}

}
