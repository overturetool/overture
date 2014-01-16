package org.overture.typechecker.assistant.definition;

import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class AValueDefinitionAssistantTC
{
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public AValueDefinitionAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

//	public static PType getType(AValueDefinition def)
//	{
//		return def.getType() != null ? def.getType()
//				: def.getExpType() != null ? def.getExpType()
//						: AstFactory.newAUnknownType(def.getLocation());
//	}


}
