package org.overture.interpreter.assistant.definition;

import org.overture.ast.expressions.PExp;
import org.overture.ast.statements.AErrorCase;
import org.overture.interpreter.assistant.expression.PExpAssistantInterpreter;

public class AErrorCaseAssistantIntepreter
{

	public static PExp findExpression(AErrorCase err, int lineno)
	{
		PExp found = PExpAssistantInterpreter.findExpression(err.getLeft(),lineno);
		if (found != null) return found;
		return PExpAssistantInterpreter.findExpression(err.getRight(),lineno);
	}

}
