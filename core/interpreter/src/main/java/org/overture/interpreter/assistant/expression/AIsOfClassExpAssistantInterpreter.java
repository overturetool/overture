package org.overture.interpreter.assistant.expression;

import org.overture.ast.expressions.AIsOfClassExp;
import org.overture.ast.expressions.PExp;
import org.overture.interpreter.runtime.ObjectContext;
import org.overture.interpreter.values.ObjectValue;
import org.overture.interpreter.values.ValueList;
import org.overture.typechecker.assistant.expression.AIsOfClassExpAssistantTC;

public class AIsOfClassExpAssistantInterpreter extends AIsOfClassExpAssistantTC
{

	public static ValueList getValues(AIsOfClassExp exp, ObjectContext ctxt)
	{
		return PExpAssistantInterpreter.getValues(exp.getExp(),ctxt);
	}

	public static PExp findExpression(AIsOfClassExp exp, int lineno)
	{
		PExp found = PExpAssistantInterpreter.findExpressionBaseCase(exp,lineno);
		if (found != null) return found;

		return PExpAssistantInterpreter.findExpression(exp.getExp(),lineno);
	}

	public static boolean isOfClass(ObjectValue obj, String name)
	{
		if (obj.type.getName().getName().equals(name))
		{
			return true;
		}
		else
		{
			for (ObjectValue objval: obj.superobjects)
			{
				if (isOfClass(objval, name))
				{
					return true;
				}
			}
		}

		return false;
	}

}
