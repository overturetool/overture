package org.overture.interpreter.assistant.expression;

import org.overture.ast.expressions.AIsExp;
import org.overture.interpreter.runtime.ObjectContext;
import org.overture.interpreter.values.ValueList;
import org.overture.typechecker.assistant.expression.AIsExpAssistantTC;

public class AIsExpAssistantInterpreter extends AIsExpAssistantTC
{

	public static ValueList getValues(AIsExp exp, ObjectContext ctxt)
	{
		return PExpAssistantInterpreter.getValues(exp.getTest(), ctxt);
	}

}
