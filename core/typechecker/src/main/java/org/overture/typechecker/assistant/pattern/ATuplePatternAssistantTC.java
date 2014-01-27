package org.overture.typechecker.assistant.pattern;

import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class ATuplePatternAssistantTC
{
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public ATuplePatternAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}
}
