package org.overture.ast.assistant.definition;

import java.util.List;

import org.overture.ast.assistant.IAstAssistantFactory;
import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AValueDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;

public class PDefinitionAssistant
{

	protected static IAstAssistantFactory af;

	@SuppressWarnings("static-access")
	public PDefinitionAssistant(IAstAssistantFactory af)
	{
		this.af = af;
	}

	public static void setClassDefinition(PDefinition pDefinition,
			SClassDefinition def)
	{
		if (pDefinition instanceof SClassDefinition) {
			for (PDefinition d : def.getDefinitions()) {
				setClassDefinition(d, def);
			}
		} else if (pDefinition instanceof AExplicitFunctionDefinition) {
			setClassDefinitionBaseCase(pDefinition, def);
			AExplicitFunctionDefinition efd = ((AExplicitFunctionDefinition) pDefinition);
			if (efd.getPredef() != null) {
				setClassDefinition(efd.getPredef(), def);
			}
			if (efd.getPostdef() != null) {
				setClassDefinition(efd.getPostdef(), def);
			}
		} else if (pDefinition instanceof AValueDefinition) {
			setClassDefinitionBaseCase(pDefinition, def);
			AValueDefinition vd = (AValueDefinition) pDefinition;
			for (PDefinition d : vd.getDefs()) {
				setClassDefinition(d, def);
			}
		} else {
			setClassDefinitionBaseCase(pDefinition, def);
		}

	}

	public static String getName(PDefinition node)
	{
		if (node.getName() != null)
		{
			return node.getName().getName();
		}

		return null;
	}

	public static void setClassDefinitionBaseCase(PDefinition pDefinition,
			SClassDefinition def)
	{
		pDefinition.setClassDefinition(def);
	}

	public static void setClassDefinition(List<PDefinition> defs,
			SClassDefinition classDefinition)
	{
		for (PDefinition d : defs)
		{
			setClassDefinition(d, classDefinition);
		}

	}

}
