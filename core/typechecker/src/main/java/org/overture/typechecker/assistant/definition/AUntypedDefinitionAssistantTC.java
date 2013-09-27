package org.overture.typechecker.assistant.definition;

import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class AUntypedDefinitionAssistantTC
{
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public AUntypedDefinitionAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

//	public static List<PDefinition> getDefinitions(AUntypedDefinition d)
//	{
//
//		List<PDefinition> result = new Vector<PDefinition>();
//		result.add(d);
//		return result;
//	}

}
