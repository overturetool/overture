package org.overture.interpreter.assistant.expression;

import org.overture.ast.expressions.AExists1Exp;
import org.overture.ast.expressions.PExp;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.interpreter.assistant.pattern.PBindAssistantInterpreter;
import org.overture.interpreter.runtime.ObjectContext;
import org.overture.interpreter.values.ValueList;

public class AExists1ExpAssistantInterpreter // extends AExists1ExpAssistantTC
{
	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public AExists1ExpAssistantInterpreter(IInterpreterAssistantFactory af)
	{
		this.af = af;
	}

	public static ValueList getValues(AExists1Exp exp, ObjectContext ctxt)
	{
		ValueList list = PBindAssistantInterpreter.getValues(exp.getBind(), ctxt);
		list.addAll(PExpAssistantInterpreter.getValues(exp.getPredicate(), ctxt));
		return list;
	}

	public static PExp findExpression(AExists1Exp exp, int lineno)
	{
		PExp found = PExpAssistantInterpreter.findExpressionBaseCase(exp, lineno);
		if (found != null)
			return found;

		return PExpAssistantInterpreter.findExpression(exp.getPredicate(), lineno);
	}

}
