package org.overture.interpreter.assistant.expression;

import org.overture.ast.expressions.ALetBeStExp;
import org.overture.ast.expressions.PExp;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.interpreter.assistant.pattern.PMultipleBindAssistantInterpreter;
import org.overture.interpreter.runtime.ObjectContext;
import org.overture.interpreter.values.ValueList;

public class ALetBeStExpAssistantInterpreter // extends ALetBeStExpAssistantTC
{
	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public ALetBeStExpAssistantInterpreter(IInterpreterAssistantFactory af)
	{
		this.af = af;
	}

	public static ValueList getValues(ALetBeStExp exp, ObjectContext ctxt)
	{
		ValueList list = PMultipleBindAssistantInterpreter.getValues(exp.getBind(), ctxt);

		if (exp.getSuchThat() != null)
		{
			list.addAll(PExpAssistantInterpreter.getValues(exp.getSuchThat(), ctxt));
		}

		list.addAll(PExpAssistantInterpreter.getValues(exp.getValue(), ctxt));
		return list;
	}

	public static PExp findExpression(ALetBeStExp exp, int lineno)
	{
		PExp found = PExpAssistantInterpreter.findExpressionBaseCase(exp, lineno);
		if (found != null)
			return found;

		if (exp.getSuchThat() != null)
		{
			found = PExpAssistantInterpreter.findExpression(exp.getSuchThat(), lineno);
			if (found != null)
				return found;
		}

		return PExpAssistantInterpreter.findExpression(exp.getValue(), lineno);
	}

}
