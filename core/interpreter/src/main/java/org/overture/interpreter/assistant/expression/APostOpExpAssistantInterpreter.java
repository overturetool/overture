package org.overture.interpreter.assistant.expression;

import org.overture.ast.expressions.APostOpExp;
import org.overture.ast.expressions.PExp;

public class APostOpExpAssistantInterpreter
{

	public static PExp findExpression(APostOpExp exp, int lineno)
	{
		return PExpAssistantInterpreter.findExpression(exp.getPostexpression(),lineno);
	}

}
