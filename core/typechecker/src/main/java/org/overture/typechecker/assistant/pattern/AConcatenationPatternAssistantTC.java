package org.overture.typechecker.assistant.pattern;



import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class AConcatenationPatternAssistantTC
{
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public AConcatenationPatternAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}
}
