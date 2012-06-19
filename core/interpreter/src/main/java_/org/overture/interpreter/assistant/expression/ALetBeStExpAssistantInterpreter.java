package org.overture.interpreter.assistant.expression;

import org.overture.ast.expressions.ALetBeStExp;
import org.overture.interpreter.assistant.pattern.PMultipleBindAssistantInterpreter;
import org.overture.interpreter.runtime.ObjectContext;
import org.overture.interpreter.values.ValueList;
import org.overture.typechecker.assistant.expression.ALetBeStExpAssistantTC;

public class ALetBeStExpAssistantInterpreter extends ALetBeStExpAssistantTC
{

	public static ValueList getValues(ALetBeStExp exp, ObjectContext ctxt)
	{
		ValueList list = PMultipleBindAssistantInterpreter.getValues(exp.getBind(),ctxt);

		if (exp.getSuchThat() != null)
		{
			list.addAll(PExpAssistantInterpreter.getValues(exp.getSuchThat(),ctxt));
		}

		list.addAll(PExpAssistantInterpreter.getValues(exp.getValue(), ctxt));
		return list;
	}
	
}
