package org.overture.interpreter.assistant.definition;

import org.overture.interpreter.assistant.IInterpreterAssistantFactory;

public class AClassInvariantDefinitionAssistantInterpreter
{
	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public AClassInvariantDefinitionAssistantInterpreter(IInterpreterAssistantFactory af)
	{
		this.af = af;
	}

//	public static PExp findExpression(AClassInvariantDefinition def, int lineno)
//	{
//		return PExpAssistantInterpreter.findExpression(def.getExpression(), lineno);
//	}
}
