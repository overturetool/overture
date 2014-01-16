package org.overture.typechecker.assistant.definition;

import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class AExternalDefinitionAssistantTC
{
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public AExternalDefinitionAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

}
