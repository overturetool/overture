package org.overture.typechecker.assistant.definition;

import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class ASystemClassDefinitionAssistantTC
{
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public ASystemClassDefinitionAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

//	public static void implicitDefinitions(ASystemClassDefinition def,
//			Environment publicClasses)
//	{}

}
