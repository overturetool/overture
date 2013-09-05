package org.overture.interpreter.assistant.expression;

import org.overture.ast.expressions.AFuncInstatiationExp;
import org.overture.ast.expressions.PExp;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.interpreter.runtime.ObjectContext;
import org.overture.interpreter.values.ValueList;

public class AFuncInstatiationExpAssistantInterpreter // extends
// AFuncInstatiationExpAssistantTC
{
	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public AFuncInstatiationExpAssistantInterpreter(
			IInterpreterAssistantFactory af)
	{
		this.af = af;
	}

	public static ValueList getValues(AFuncInstatiationExp exp,
			ObjectContext ctxt)
	{
		return PExpAssistantInterpreter.getValues(exp.getFunction(), ctxt);
	}

	public static PExp findExpression(AFuncInstatiationExp exp, int lineno)
	{
		PExp found = PExpAssistantInterpreter.findExpressionBaseCase(exp, lineno);
		if (found != null)
			return found;

		return PExpAssistantInterpreter.findExpression(exp.getFunction(), lineno);
	}

}
