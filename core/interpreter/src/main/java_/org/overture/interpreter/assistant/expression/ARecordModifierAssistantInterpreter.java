package org.overture.interpreter.assistant.expression;


import org.overture.ast.expressions.ARecordModifier;
import org.overture.interpreter.runtime.ObjectContext;
import org.overture.interpreter.values.ValueList;
import org.overture.typechecker.assistant.expression.ARecordModifierAssistantTC;

public class ARecordModifierAssistantInterpreter extends
		ARecordModifierAssistantTC
{

	public static ValueList getValues(ARecordModifier rm,
			ObjectContext ctxt)
	{
		return PExpAssistantInterpreter.getValues(rm.getValue(),ctxt);
	}
	
}
