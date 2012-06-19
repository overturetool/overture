package org.overture.interpreter.assistant.expression;

import org.overture.ast.expressions.AIotaExp;
import org.overture.interpreter.assistant.pattern.PBindAssistantInterpreter;
import org.overture.interpreter.runtime.ObjectContext;
import org.overture.interpreter.values.ValueList;
import org.overture.typechecker.assistant.expression.AIotaExpAssistantTC;

public class AIotaExpAssistantInterpreter extends AIotaExpAssistantTC
{

	public static ValueList getValues(AIotaExp exp, ObjectContext ctxt)
	{
		ValueList list = PBindAssistantInterpreter.getValues(exp.getBind(),ctxt);
		list.addAll(PExpAssistantInterpreter.getValues(exp.getPredicate(),ctxt));
		return list;
	}
	
}
