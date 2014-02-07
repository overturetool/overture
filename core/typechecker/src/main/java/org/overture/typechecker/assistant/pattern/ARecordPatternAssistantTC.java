package org.overture.typechecker.assistant.pattern;

import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class ARecordPatternAssistantTC
{
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public ARecordPatternAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}
}
