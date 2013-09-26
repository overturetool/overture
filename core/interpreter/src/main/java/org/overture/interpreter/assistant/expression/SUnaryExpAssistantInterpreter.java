package org.overture.interpreter.assistant.expression;

import org.overture.ast.expressions.PExp;
import org.overture.ast.expressions.SUnaryExp;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.interpreter.runtime.ObjectContext;
import org.overture.interpreter.values.ValueList;
import org.overture.typechecker.assistant.expression.SUnaryExpAssistantTC;

public class SUnaryExpAssistantInterpreter extends SUnaryExpAssistantTC
{
	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public SUnaryExpAssistantInterpreter(IInterpreterAssistantFactory af)
	{
		super(af);
		this.af = af;
	}

	public static ValueList getValues(SUnaryExp exp, ObjectContext ctxt)
	{
		return PExpAssistantInterpreter.getValues(exp.getExp(), ctxt);
	}

	public static PExp findExpression(SUnaryExp exp, int lineno)
	{
		PExp found = PExpAssistantInterpreter.findExpressionBaseCase(exp, lineno);
		if (found != null)
			return found;

		return PExpAssistantInterpreter.findExpression(exp.getExp(), lineno);
	}

}
