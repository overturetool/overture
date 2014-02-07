package org.overture.typechecker.assistant.pattern;

import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class ASeqPatternAssistantTC
{
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public ASeqPatternAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}
}
