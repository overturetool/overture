package org.overture.interpreter.assistant.expression;

import org.overture.ast.expressions.AFieldNumberExp;
import org.overture.ast.expressions.PExp;
import org.overture.interpreter.runtime.ObjectContext;
import org.overture.interpreter.values.ValueList;
import org.overture.typechecker.assistant.expression.AFieldNumberExpAssistantTC;

public class AFieldNumberExpAssistantInterpreter extends
		AFieldNumberExpAssistantTC
{

	public static ValueList getValues(AFieldNumberExp exp, ObjectContext ctxt)
	{
		return PExpAssistantInterpreter.getValues(exp.getTuple(), ctxt);
	}

	public static PExp findExpression(AFieldNumberExp exp, int lineno)
	{
		PExp found = PExpAssistantInterpreter.findExpressionBaseCase(exp,lineno);
		if (found != null) return found;

		return PExpAssistantInterpreter.findExpression(exp.getTuple(),lineno);
	}

}
