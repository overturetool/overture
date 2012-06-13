package org.overture.interpreter.assistant.expression;

import java.util.List;

import org.overture.ast.expressions.PExp;
import org.overture.interpreter.runtime.ObjectContext;
import org.overture.interpreter.values.ValueList;
import org.overture.typechecker.assistant.expression.PExpAssistantTC;

public class PExpAssistantInterpreter extends PExpAssistantTC
{

	public static ValueList getValues(PExp guard, ObjectContext ctxt)
	{
		//TODO: not implemented
		assert false : "not implemented";
		return null;
	}

	public static Object findExpression(PExp guard, int line)
	{
		//TODO: not implemented
		assert false : "not implemented";
		return null;
	}

	public static List<PExp> getSubExpressions(PExp guard)
	{
		//TODO: not implemented
		assert false : "not implemented";
		return null;
	}

}
