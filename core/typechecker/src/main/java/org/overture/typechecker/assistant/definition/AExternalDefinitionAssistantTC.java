package org.overture.typechecker.assistant.definition;

import org.overture.ast.definitions.AExternalDefinition;
import org.overture.ast.types.PType;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class AExternalDefinitionAssistantTC
{
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public AExternalDefinitionAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}


	public static PType getType(AExternalDefinition def)
	{
		return af.createPDefinitionAssistant().getType(def.getState());
	}

	public static boolean isUsed(AExternalDefinition u)
	{
		return PDefinitionAssistantTC.isUsed(u.getState());

	}

}
