package org.overture.interpreter.assistant.expression;

import org.overture.ast.expressions.ATupleExp;
import org.overture.interpreter.runtime.ObjectContext;
import org.overture.interpreter.values.ValueList;
import org.overture.typechecker.assistant.expression.ATupleExpAssistantTC;

public class ATupleExpAssistantInterpreter extends ATupleExpAssistantTC
{

	public static ValueList getValues(ATupleExp exp, ObjectContext ctxt)
	{
		return PExpAssistantInterpreter.getValues(exp.getArgs(),ctxt);
	}

}
