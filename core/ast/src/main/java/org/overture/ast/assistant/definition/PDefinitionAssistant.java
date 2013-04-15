package org.overture.ast.assistant.definition;

import java.util.List;

import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AValueDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;

public class PDefinitionAssistant {

	public static void setClassDefinition(PDefinition pDefinition,
			SClassDefinition def) {
		switch (pDefinition.kindPDefinition()) {
		case SClassDefinition.kindPDefinition:
			for (PDefinition d : def.getDefinitions())
			{
				setClassDefinition(d,def);
			}
			break;		
		case AExplicitFunctionDefinition.kindPDefinition:
			setClassDefinitionBaseCase(pDefinition, def);			
			AExplicitFunctionDefinition efd = ((AExplicitFunctionDefinition)pDefinition);
			if(efd.getPredef() != null)
			{
				setClassDefinition(efd.getPredef(), def);
			}
			if(efd.getPostdef() != null)
			{
				setClassDefinition(efd.getPostdef(), def);
			}
			break;					
		case AValueDefinition.kindPDefinition:
			setClassDefinitionBaseCase(pDefinition, def);	
			AValueDefinition vd = (AValueDefinition) pDefinition;
			for (PDefinition d : vd.getDefs())
			{
				setClassDefinition(d,def);
			}
			break;
		default:
			setClassDefinitionBaseCase(pDefinition, def);
			break;
		}
		
	}

	public static void setClassDefinitionBaseCase(PDefinition pDefinition,SClassDefinition def)
	{
		pDefinition.setClassDefinition(def);
	}

	public static void setClassDefinition(List<PDefinition> defs,
			SClassDefinition classDefinition) {
		for (PDefinition d : defs) {
			setClassDefinition(d,classDefinition);
		}

	}
	
}
