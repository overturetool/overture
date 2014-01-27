package org.overture.typechecker.assistant.type;

import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class SSeqTypeAssistantTC
{
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public SSeqTypeAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}
}
