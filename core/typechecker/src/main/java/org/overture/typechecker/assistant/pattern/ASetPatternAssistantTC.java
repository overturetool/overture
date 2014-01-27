package org.overture.typechecker.assistant.pattern;

import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class ASetPatternAssistantTC
{
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public ASetPatternAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}
}
