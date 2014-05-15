package org.overture.interpreter.assistant.expression;

import org.overture.ast.expressions.ALambdaExp;
import org.overture.ast.expressions.PExp;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.interpreter.runtime.ObjectContext;
import org.overture.interpreter.values.ValueList;

public class ALambdaExpAssistantInterpreter // extends ALambdaExpAssistantTC
{
	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public ALambdaExpAssistantInterpreter(IInterpreterAssistantFactory af)
	{
		this.af = af;
	}

//	public static ValueList getValues(ALambdaExp exp, ObjectContext ctxt)
//	{
//		return PExpAssistantInterpreter.getValues(exp.getExpression(), ctxt);
//	}

//	public static PExp findExpression(ALambdaExp exp, int lineno)
//	{
//		PExp found = PExpAssistantInterpreter.findExpressionBaseCase(exp, lineno);
//		if (found != null)
//			return found;
//
//		return PExpAssistantInterpreter.findExpression(exp.getExpression(), lineno);
//	}

}
