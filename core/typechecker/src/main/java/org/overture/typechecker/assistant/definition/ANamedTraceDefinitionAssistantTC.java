package org.overture.typechecker.assistant.definition;

import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class ANamedTraceDefinitionAssistantTC
{
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public ANamedTraceDefinitionAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

}
