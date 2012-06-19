package org.overture.interpreter.assistant.expression;

import org.overture.ast.expressions.AMkBasicExp;
import org.overture.interpreter.runtime.ObjectContext;
import org.overture.interpreter.values.ValueList;
import org.overture.typechecker.assistant.expression.AMkBasicExpAssistantTC;

public class AMkBasicExpAssistantInterpreter extends AMkBasicExpAssistantTC
{

	public static ValueList getValues(AMkBasicExp exp, ObjectContext ctxt)
	{
		return PExpAssistantInterpreter.getValues(exp.getArg(),ctxt);
	}

}
