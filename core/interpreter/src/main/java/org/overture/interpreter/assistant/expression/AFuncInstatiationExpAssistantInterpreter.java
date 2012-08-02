package org.overture.interpreter.assistant.expression;

import org.overture.ast.expressions.AFuncInstatiationExp;
import org.overture.ast.expressions.PExp;
import org.overture.interpreter.runtime.ObjectContext;
import org.overture.interpreter.values.ValueList;
import org.overture.typechecker.assistant.expression.AFuncInstatiationExpAssistantTC;

public class AFuncInstatiationExpAssistantInterpreter extends
		AFuncInstatiationExpAssistantTC
{

	public static ValueList getValues(AFuncInstatiationExp exp,
			ObjectContext ctxt)
	{
		return PExpAssistantInterpreter.getValues(exp.getFunction(),ctxt);
	}

	public static PExp findExpression(AFuncInstatiationExp exp, int lineno)
	{
		PExp found = PExpAssistantInterpreter.findExpressionBaseCase(exp,lineno);
		if (found != null) return found;

		return PExpAssistantInterpreter.findExpression(exp.getFunction(),lineno);
	}

}
