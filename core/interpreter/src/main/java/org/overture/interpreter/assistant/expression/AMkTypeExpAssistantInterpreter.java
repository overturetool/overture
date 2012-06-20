package org.overture.interpreter.assistant.expression;

import org.overture.ast.expressions.AMkTypeExp;
import org.overture.ast.expressions.PExp;
import org.overture.interpreter.runtime.ObjectContext;
import org.overture.interpreter.values.ValueList;
import org.overture.typechecker.assistant.expression.AMkTypeExpAssistantTC;

public class AMkTypeExpAssistantInterpreter extends AMkTypeExpAssistantTC
{

	public static ValueList getValues(AMkTypeExp exp, ObjectContext ctxt)
	{
		return PExpAssistantInterpreter.getValues(exp.getArgs(),ctxt);
	}

	public static PExp findExpression(AMkTypeExp exp, int lineno)
	{
		PExp found = PExpAssistantInterpreter.findExpressionBaseCase(exp,lineno);
		if (found != null) return found;

		return PExpAssistantInterpreter.findExpression(exp.getArgs(),lineno);
	}

}
