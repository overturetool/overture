package org.overture.interpreter.assistant.expression;

import org.overture.ast.expressions.AIsExp;
import org.overture.ast.expressions.PExp;
import org.overture.interpreter.runtime.ObjectContext;
import org.overture.interpreter.values.ValueList;
import org.overture.typechecker.assistant.expression.AIsExpAssistantTC;

public class AIsExpAssistantInterpreter extends AIsExpAssistantTC
{

	public static ValueList getValues(AIsExp exp, ObjectContext ctxt)
	{
		return PExpAssistantInterpreter.getValues(exp.getTest(), ctxt);
	}

	public static PExp findExpression(AIsExp exp, int lineno)
	{
		PExp found = PExpAssistantInterpreter.findExpressionBaseCase(exp,lineno);
		if (found != null) return found;

		return PExpAssistantInterpreter.findExpression(exp.getTest(),lineno);
	}

}
