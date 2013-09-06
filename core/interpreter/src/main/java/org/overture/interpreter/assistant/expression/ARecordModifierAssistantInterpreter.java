package org.overture.interpreter.assistant.expression;

import org.overture.ast.expressions.ARecordModifier;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.interpreter.runtime.ObjectContext;
import org.overture.interpreter.values.ValueList;

public class ARecordModifierAssistantInterpreter 
{
	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public ARecordModifierAssistantInterpreter(IInterpreterAssistantFactory af)
	{
		this.af = af;
	}

	public static ValueList getValues(ARecordModifier rm, ObjectContext ctxt)
	{
		return PExpAssistantInterpreter.getValues(rm.getValue(), ctxt);
	}

}
