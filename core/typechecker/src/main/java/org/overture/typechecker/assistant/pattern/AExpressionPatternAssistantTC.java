package org.overture.typechecker.assistant.pattern;

import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class AExpressionPatternAssistantTC
{
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public AExpressionPatternAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}
}
