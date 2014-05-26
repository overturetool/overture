package org.overture.interpreter.assistant.definition;

import org.overture.interpreter.assistant.IInterpreterAssistantFactory;

public class AErrorCaseAssistantInterpreter
{
	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public AErrorCaseAssistantInterpreter(IInterpreterAssistantFactory af)
	{
		this.af = af;
	}

}
