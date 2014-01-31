package org.overture.typechecker.assistant.type;

import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class ASetTypeAssistantTC
{
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public ASetTypeAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

}
