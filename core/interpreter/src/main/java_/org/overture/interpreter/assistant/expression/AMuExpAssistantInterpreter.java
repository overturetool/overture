package org.overture.interpreter.assistant.expression;

import org.overture.ast.expressions.AMuExp;
import org.overture.ast.expressions.ARecordModifier;
import org.overture.interpreter.runtime.ObjectContext;
import org.overture.interpreter.values.ValueList;
import org.overture.typechecker.assistant.expression.AMuExpAssistantTC;

public class AMuExpAssistantInterpreter extends AMuExpAssistantTC
{

	public static ValueList getValues(AMuExp exp, ObjectContext ctxt)
	{
		ValueList list = PExpAssistantInterpreter.getValues(exp.getRecord(), ctxt);

		for (ARecordModifier rm: exp.getModifiers())
		{
			list.addAll(ARecordModifierAssistantInterpreter.getValues(rm,ctxt));
		}

		return list;
	}

}
