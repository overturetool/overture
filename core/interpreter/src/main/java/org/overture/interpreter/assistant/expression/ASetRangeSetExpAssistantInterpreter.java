package org.overture.interpreter.assistant.expression;

import org.overture.ast.expressions.ASetRangeSetExp;
import org.overture.ast.expressions.PExp;

public class ASetRangeSetExpAssistantInterpreter
{

	public static PExp findExpression(ASetRangeSetExp exp, int lineno)
	{
		PExp found = PExpAssistantInterpreter.findExpressionBaseCase(exp, lineno);
		if (found != null) return found;

		found = PExpAssistantInterpreter.findExpression(exp.getFirst(),lineno);
		if (found != null) return found;

		found = PExpAssistantInterpreter.findExpression(exp.getLast(),lineno);
		if (found != null) return found;

		return null;
	}

}
