package org.overture.typechecker.assistant.definition;

import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class AMultiBindListDefinitionAssistantTC
{
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public AMultiBindListDefinitionAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}


//	public static PType getType(AMultiBindListDefinition def)
//	{
//		PTypeList types = new PTypeList();
//
//		for (PDefinition definition : def.getDefs())
//		{
//			types.add(definition.getType());
//		}
//
//		AUnionType result = AstFactory.newAUnionType(def.getLocation(), types);
//
//		return result;
//	}

}
