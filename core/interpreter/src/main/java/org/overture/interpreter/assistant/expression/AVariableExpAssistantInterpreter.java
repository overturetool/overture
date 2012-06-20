package org.overture.interpreter.assistant.expression;

import org.overture.ast.expressions.AVariableExp;
import org.overture.interpreter.runtime.ObjectContext;
import org.overture.interpreter.values.UpdatableValue;
import org.overture.interpreter.values.Value;
import org.overture.interpreter.values.ValueList;
import org.overture.typechecker.assistant.expression.AVariableExpAssistantTC;

public class AVariableExpAssistantInterpreter extends AVariableExpAssistantTC
{

	public static ValueList getVariable(AVariableExp exp, ObjectContext ctxt)
	{
		Value v = ctxt.check(exp.getName());

		if (v == null || !(v instanceof UpdatableValue))
		{
			return new ValueList();
		}
		else
		{
			return new ValueList(v);
		}
	}

}
