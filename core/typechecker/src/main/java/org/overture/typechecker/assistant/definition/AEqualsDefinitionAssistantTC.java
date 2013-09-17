package org.overture.typechecker.assistant.definition;

import org.overture.ast.definitions.AEqualsDefinition;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.types.PType;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class AEqualsDefinitionAssistantTC
{
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public AEqualsDefinitionAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	public static void unusedCheck(AEqualsDefinition d)
	{

		if (d.getDefs() != null)
		{
			PDefinitionListAssistantTC.unusedCheck(d.getDefs());
		}

	}

	public static PType getType(AEqualsDefinition def)
	{
		return def.getDefType() != null ? def.getDefType()
				: AstFactory.newAUnknownType(def.getLocation());
	}


}
