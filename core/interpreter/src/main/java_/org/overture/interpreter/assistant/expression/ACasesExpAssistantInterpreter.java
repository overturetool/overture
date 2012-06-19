package org.overture.interpreter.assistant.expression;

import org.overture.ast.expressions.ACaseAlternative;
import org.overture.ast.expressions.ACasesExp;
import org.overture.interpreter.runtime.ObjectContext;
import org.overture.interpreter.values.ValueList;
import org.overture.typechecker.assistant.expression.ACasesExpAssistantTC;

public class ACasesExpAssistantInterpreter extends ACasesExpAssistantTC
{

	public static ValueList getValues(ACasesExp exp, ObjectContext ctxt)
	{
		ValueList list = PExpAssistantInterpreter.getValues(exp.getExpression(),ctxt);

		for (ACaseAlternative c: exp.getCases())
		{
			list.addAll(ACaseAlternativeAssistantInterpreter.getValues(c,ctxt));
		}

		if (exp.getOthers() != null)
		{
			list.addAll(PExpAssistantInterpreter.getValues(exp.getOthers(), ctxt));
		}

		return list;
	}

}
