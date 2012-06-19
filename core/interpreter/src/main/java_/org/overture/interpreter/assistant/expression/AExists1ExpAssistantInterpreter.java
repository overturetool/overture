package org.overture.interpreter.assistant.expression;

import org.overture.ast.expressions.AExists1Exp;
import org.overture.interpreter.assistant.pattern.PBindAssistantInterpreter;
import org.overture.interpreter.runtime.ObjectContext;
import org.overture.interpreter.values.ValueList;
import org.overture.typechecker.assistant.expression.AExists1ExpAssistantTC;

public class AExists1ExpAssistantInterpreter extends AExists1ExpAssistantTC
{

	public static ValueList getValues(AExists1Exp exp, ObjectContext ctxt)
	{
		ValueList list = PBindAssistantInterpreter.getValues(exp.getBind(), ctxt);
		list.addAll(PExpAssistantInterpreter.getValues(exp.getPredicate(),ctxt));
		return list;
	}

}
