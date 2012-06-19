package org.overture.interpreter.assistant.expression;

import org.overture.ast.expressions.ADefExp;
import org.overture.interpreter.assistant.definition.PDefinitionListAssistantInterpreter;
import org.overture.interpreter.runtime.ObjectContext;
import org.overture.interpreter.values.ValueList;

public class ADefExpAssistantInterpreter
{

	public static ValueList getValues(ADefExp exp, ObjectContext ctxt)
	{
		ValueList list = PDefinitionListAssistantInterpreter.getValues(exp.getLocalDefs(),ctxt);
		list.addAll(PExpAssistantInterpreter.getValues(exp.getExpression(), ctxt));
		return list;
	}

}
