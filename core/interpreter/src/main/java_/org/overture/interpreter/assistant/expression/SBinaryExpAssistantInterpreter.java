package org.overture.interpreter.assistant.expression;

import org.overture.ast.expressions.SBinaryExp;
import org.overture.interpreter.runtime.ObjectContext;
import org.overture.interpreter.values.ValueList;
import org.overture.typechecker.assistant.expression.SBinaryExpAssistantTC;

public class SBinaryExpAssistantInterpreter extends SBinaryExpAssistantTC
{

	public static ValueList getValues(SBinaryExp exp, ObjectContext ctxt)
	{
		ValueList list = PExpAssistantInterpreter.getValues(exp.getLeft(),ctxt);
		list.addAll(PExpAssistantInterpreter.getValues(exp.getRight(),ctxt));
		return list;
	}

}
