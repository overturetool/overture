package org.overture.typechecker.assistant.definition;

import org.overture.ast.definitions.ALocalDefinition;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class ALocalDefinitionAssistantTC
{

	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public ALocalDefinitionAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	public static void setValueDefinition(ALocalDefinition ld)
	{
		ld.setValueDefinition(true);

	}

}
