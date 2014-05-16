package org.overture.interpreter.assistant.expression;

import org.overture.ast.expressions.AMkBasicExp;
import org.overture.ast.expressions.PExp;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.interpreter.runtime.ObjectContext;
import org.overture.interpreter.values.ValueList;

public class AMkBasicExpAssistantInterpreter // extends AMkBasicExpAssistantTC
{
	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public AMkBasicExpAssistantInterpreter(IInterpreterAssistantFactory af)
	{
		this.af = af;
	}

//	public static ValueList getValues(AMkBasicExp exp, ObjectContext ctxt)
//	{
//		return PExpAssistantInterpreter.getValues(exp.getArg(), ctxt);
//	}

//	public static PExp findExpression(AMkBasicExp exp, int lineno)
//	{
//		PExp found = PExpAssistantInterpreter.findExpressionBaseCase(exp, lineno);
//		if (found != null)
//			return found;
//
//		return PExpAssistantInterpreter.findExpression(exp.getArg(), lineno);
//	}

}
