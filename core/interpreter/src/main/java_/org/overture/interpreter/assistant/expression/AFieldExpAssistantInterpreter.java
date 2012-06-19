package org.overture.interpreter.assistant.expression;

import org.overture.ast.expressions.AFieldExp;
import org.overture.ast.expressions.PExp;
import org.overture.interpreter.runtime.ObjectContext;
import org.overture.interpreter.values.ValueList;
import org.overture.typechecker.assistant.expression.AFieldExpAssistantTC;

public class AFieldExpAssistantInterpreter extends AFieldExpAssistantTC
{

	public static ValueList getValues(AFieldExp exp, ObjectContext ctxt)
	{
		return PExpAssistantInterpreter.getValues(exp.getObject(),ctxt);
	}

	public static PExp findExpression(AFieldExp exp, int lineno)
	{
		PExp found = PExpAssistantInterpreter.findExpression(exp,lineno);
		if (found != null) return found;

		return PExpAssistantInterpreter.findExpression(exp.getObject(),lineno);
	}

}
