package org.overture.interpreter.assistant.expression;

import org.overture.ast.expressions.AIsOfClassExp;
import org.overture.interpreter.runtime.ObjectContext;
import org.overture.interpreter.values.ValueList;
import org.overture.typechecker.assistant.expression.AIsOfClassExpAssistantTC;

public class AIsOfClassExpAssistantInterpreter extends AIsOfClassExpAssistantTC
{

	public static ValueList getValues(AIsOfClassExp exp, ObjectContext ctxt)
	{
		return PExpAssistantInterpreter.getValues(exp.getExp(),ctxt);
	}

}
