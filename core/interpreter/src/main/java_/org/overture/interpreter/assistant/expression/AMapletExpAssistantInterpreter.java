package org.overture.interpreter.assistant.expression;


import org.overture.ast.expressions.AMapletExp;
import org.overture.interpreter.runtime.ObjectContext;
import org.overture.interpreter.values.ValueList;
import org.overture.typechecker.assistant.expression.AMapletExpAssistantTC;

public class AMapletExpAssistantInterpreter extends AMapletExpAssistantTC
{

	public static ValueList getValues(AMapletExp maplet,
			ObjectContext ctxt)
	{
		ValueList list = PExpAssistantInterpreter.getValues(maplet.getLeft(),ctxt);
		list.addAll(PExpAssistantInterpreter.getValues(maplet.getRight(),ctxt));
		return list;
	}

}
