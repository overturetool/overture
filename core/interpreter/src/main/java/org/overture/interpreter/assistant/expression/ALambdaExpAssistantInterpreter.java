package org.overture.interpreter.assistant.expression;

import org.overture.ast.expressions.ALambdaExp;
import org.overture.ast.expressions.PExp;
import org.overture.interpreter.runtime.ObjectContext;
import org.overture.interpreter.values.ValueList;
import org.overture.typechecker.assistant.expression.ALambdaExpAssistantTC;

public class ALambdaExpAssistantInterpreter extends ALambdaExpAssistantTC
{

	public static ValueList getValues(ALambdaExp exp, ObjectContext ctxt)
	{
		return PExpAssistantInterpreter.getValues(exp.getExpression(), ctxt);
	}

	public static PExp findExpression(ALambdaExp exp, int lineno)
	{
		PExp found = PExpAssistantInterpreter.findExpressionBaseCase(exp,lineno);
		if (found != null) return found;

		return PExpAssistantInterpreter.findExpression(exp.getExpression(),lineno);
	}

}
