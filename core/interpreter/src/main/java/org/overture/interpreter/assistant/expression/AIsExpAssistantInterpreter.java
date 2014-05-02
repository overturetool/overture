package org.overture.interpreter.assistant.expression;

import org.overture.ast.expressions.AIsExp;
import org.overture.ast.expressions.PExp;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.interpreter.runtime.ObjectContext;
import org.overture.interpreter.values.ValueList;

public class AIsExpAssistantInterpreter // extends AIsExpAssistantTC
{
	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public AIsExpAssistantInterpreter(IInterpreterAssistantFactory af)
	{
		this.af = af;
	}

//	public static ValueList getValues(AIsExp exp, ObjectContext ctxt)
//	{
//		return PExpAssistantInterpreter.getValues(exp.getTest(), ctxt);
//	}

	public static PExp findExpression(AIsExp exp, int lineno)
	{
		PExp found = PExpAssistantInterpreter.findExpressionBaseCase(exp, lineno);
		if (found != null)
			return found;

		return PExpAssistantInterpreter.findExpression(exp.getTest(), lineno);
	}

}
