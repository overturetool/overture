package org.overture.interpreter.assistant.expression;

import org.overture.ast.expressions.ALetDefExp;
import org.overture.interpreter.assistant.definition.PDefinitionListAssistantInterpreter;
import org.overture.interpreter.runtime.ObjectContext;
import org.overture.interpreter.values.ValueList;
import org.overture.typechecker.assistant.expression.ALetDefExpAssistantTC;

public class ALetDefExpAssistantInterpreter extends ALetDefExpAssistantTC
{

	public static ValueList getValues(ALetDefExp exp, ObjectContext ctxt)
	{
		ValueList list = PDefinitionListAssistantInterpreter.getValues(exp.getLocalDefs(),ctxt);
		list.addAll(PExpAssistantInterpreter.getValues(exp.getExpression(), ctxt));
		return list;
	}

}
