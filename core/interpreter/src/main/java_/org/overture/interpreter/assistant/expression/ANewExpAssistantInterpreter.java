package org.overture.interpreter.assistant.expression;

import org.overture.ast.expressions.ANewExp;
import org.overture.ast.expressions.PExp;
import org.overture.interpreter.runtime.ObjectContext;
import org.overture.interpreter.values.ValueList;
import org.overture.typechecker.assistant.expression.ANewExpAssistantTC;

public class ANewExpAssistantInterpreter extends ANewExpAssistantTC
{

	public static ValueList getValues(ANewExp exp, ObjectContext ctxt)
	{
		return PExpAssistantInterpreter.getValues(exp.getArgs(), ctxt);
	}

	public static PExp findExpression(ANewExp exp, int lineno)
	{
		PExp found = PExpAssistantInterpreter.findExpressionBaseCase(exp,lineno);
		if (found != null) return found;

		return PExpAssistantInterpreter.findExpression(exp.getArgs(),lineno);
	}

}
