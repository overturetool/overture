package org.overture.interpreter.assistant.expression;

import org.overture.ast.expressions.ASetRangeSetExp;
import org.overture.ast.expressions.PExp;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;

public class ASetRangeSetExpAssistantInterpreter
{
	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public ASetRangeSetExpAssistantInterpreter(IInterpreterAssistantFactory af)
	{
		this.af = af;
	}

//	public static PExp findExpression(ASetRangeSetExp exp, int lineno)
//	{
//		PExp found = PExpAssistantInterpreter.findExpressionBaseCase(exp, lineno);
//		if (found != null)
//			return found;
//
//		found = PExpAssistantInterpreter.findExpression(exp.getFirst(), lineno);
//		if (found != null)
//			return found;
//
//		found = PExpAssistantInterpreter.findExpression(exp.getLast(), lineno);
//		if (found != null)
//			return found;
//
//		return null;
//	}

}
