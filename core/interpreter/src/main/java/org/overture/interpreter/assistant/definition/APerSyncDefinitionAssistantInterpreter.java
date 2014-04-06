package org.overture.interpreter.assistant.definition;

import org.overture.interpreter.assistant.IInterpreterAssistantFactory;

public class APerSyncDefinitionAssistantInterpreter
{
	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public APerSyncDefinitionAssistantInterpreter(
			IInterpreterAssistantFactory af)
	{
		this.af = af;
	}

//	public static PExp findExpression(APerSyncDefinition d, int lineno)
//	{
//		return PExpAssistantInterpreter.findExpression(d.getGuard(), lineno);
//	}

}
