package org.overture.interpreter.assistant.expression;

import org.overture.ast.expressions.AApplyExp;
import org.overture.interpreter.runtime.ObjectContext;
import org.overture.interpreter.values.ValueList;

public class AApplyExpAssistantInterpreter
{

	public static ValueList getValues(AApplyExp exp, ObjectContext ctxt)
	{
		ValueList list = PExpAssistantInterpreter.getValues(exp.getArgs(),ctxt);
		list.addAll(PExpAssistantInterpreter.getValues(exp.getRoot(),ctxt));
		return list;
	}

}
