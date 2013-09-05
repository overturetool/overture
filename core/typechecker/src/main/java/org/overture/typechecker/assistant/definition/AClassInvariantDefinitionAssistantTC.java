package org.overture.typechecker.assistant.definition;

import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class AClassInvariantDefinitionAssistantTC
{
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public AClassInvariantDefinitionAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}
}
