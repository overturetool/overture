package org.overture.interpreter.assistant.expression;

import org.overture.ast.expressions.AElseIfExp;
import org.overture.interpreter.runtime.ObjectContext;
import org.overture.interpreter.values.ValueList;
import org.overture.typechecker.assistant.expression.AElseIfExpAssistantTC;

public class AElseIfExpAssistantInterpreter extends AElseIfExpAssistantTC
{

	public static ValueList getValues(AElseIfExp exp, ObjectContext ctxt)
	{
		ValueList list = PExpAssistantInterpreter.getValues(exp.getElseIf(), ctxt);
		list.addAll(PExpAssistantInterpreter.getValues(exp.getThen(), ctxt));
		return list;
	}

}
