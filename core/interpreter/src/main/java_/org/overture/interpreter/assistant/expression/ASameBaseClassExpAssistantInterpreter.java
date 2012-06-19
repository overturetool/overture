package org.overture.interpreter.assistant.expression;

import org.overture.ast.expressions.ASameBaseClassExp;
import org.overture.interpreter.runtime.ObjectContext;
import org.overture.interpreter.values.ValueList;
import org.overture.typechecker.assistant.expression.ASameBaseClassExpAssistantTC;

public class ASameBaseClassExpAssistantInterpreter extends
		ASameBaseClassExpAssistantTC
{

	public static ValueList getValues(ASameBaseClassExp exp, ObjectContext ctxt)
	{
		ValueList list = PExpAssistantInterpreter.getValues(exp.getLeft(), ctxt);
		list.addAll(PExpAssistantInterpreter.getValues(exp.getRight(), ctxt));
		return list;
	}

}
