package org.overture.typechecker.assistant.type;

import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class AProductTypeAssistantTC
{
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public AProductTypeAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}
}
