package org.overture.interpreter.assistant.expression;

import org.overture.ast.expressions.ANarrowExp;
import org.overture.ast.expressions.PExp;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.interpreter.runtime.ObjectContext;
import org.overture.interpreter.values.ValueList;

public class ANarrowExpAssistantInterpreter // extends ANarrowExpAssistantTC
{
	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public ANarrowExpAssistantInterpreter(IInterpreterAssistantFactory af)
	{
		this.af = af;
	}

//	public static ValueList getValues(ANarrowExp exp, ObjectContext ctxt)
//	{
//		return PExpAssistantInterpreter.getValues(exp.getTest(), ctxt);
//	}

//	public static PExp findExpression(ANarrowExp exp, int lineno)
//	{
//		PExp found = PExpAssistantInterpreter.findExpression(exp, lineno);
//
//		if (found != null)
//			return found;
//
//		return PExpAssistantInterpreter.findExpression(exp.getTest(), lineno);
//	}

}
