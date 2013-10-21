package org.overture.typechecker.assistant.definition;

import org.overture.ast.definitions.AInheritedDefinition;
import org.overture.ast.definitions.AUntypedDefinition;
import org.overture.ast.types.PType;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class AInheritedDefinitionAssistantTC
{
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public AInheritedDefinitionAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}


	public static void checkSuperDefinition(AInheritedDefinition d)
	{
		// This is used to get over the case where an inherited definition
		// is a ValueDefinition that has since been replaced with a new
		// LocalDefinition. It would be better to somehow list the
		// inherited definitions that refer to a LocalDefinition and update
		// them...

		if (d.getSuperdef() instanceof AUntypedDefinition)
		{
			if (d.getClassDefinition() != null)
			{
				d.setSuperdef(PDefinitionAssistantTC.findName(d.getClassDefinition(), d.getSuperdef().getName(), d.getNameScope()));
			}
		}
	}

	public static PType getType(AInheritedDefinition def)
	{
		checkSuperDefinition(def);
		return af.createPDefinitionAssistant().getType(def.getSuperdef());
	}

	public static boolean isUsed(AInheritedDefinition u)
	{
		return PDefinitionAssistantTC.isUsed(u.getSuperdef());
	}

}
