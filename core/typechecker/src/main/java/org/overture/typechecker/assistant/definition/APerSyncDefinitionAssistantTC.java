package org.overture.typechecker.assistant.definition;

import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class APerSyncDefinitionAssistantTC
{
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public APerSyncDefinitionAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

}
