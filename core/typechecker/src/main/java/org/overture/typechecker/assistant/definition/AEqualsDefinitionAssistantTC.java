package org.overture.typechecker.assistant.definition;

import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class AEqualsDefinitionAssistantTC
{
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public AEqualsDefinitionAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}
}
