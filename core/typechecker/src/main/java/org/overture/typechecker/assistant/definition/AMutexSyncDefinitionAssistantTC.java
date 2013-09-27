package org.overture.typechecker.assistant.definition;

import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class AMutexSyncDefinitionAssistantTC
{
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public AMutexSyncDefinitionAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

}
