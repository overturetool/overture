package org.overture.typechecker.assistant.definition;

import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class AMultiBindListDefinitionAssistantTC
{
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public AMultiBindListDefinitionAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

}
