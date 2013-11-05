package org.overture.typechecker.assistant.pattern;

import org.overture.ast.patterns.ASetMultipleBind;
import org.overture.ast.types.PType;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class ASetMultipleBindAssistantTC
{
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public ASetMultipleBindAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	public static PType getPossibleType(ASetMultipleBind mb)
	{
		return PPatternListAssistantTC.getPossibleType(mb.getPlist(), mb.getLocation());
	}

}
