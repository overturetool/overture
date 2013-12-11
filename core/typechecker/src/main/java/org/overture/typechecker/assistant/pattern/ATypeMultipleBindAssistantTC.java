package org.overture.typechecker.assistant.pattern;

import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class ATypeMultipleBindAssistantTC
{
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public ATypeMultipleBindAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

//	public static PType getPossibleType(ATypeMultipleBind mb)
//	{
//		return PPatternListAssistantTC.getPossibleType(mb.getPlist(), mb.getLocation());
//	}

}
