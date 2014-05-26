package org.overture.codegen.assistant;

import org.overture.codegen.cgast.statements.AIdentifierStateDesignatorCG;
import org.overture.codegen.cgast.types.PTypeCG;

public class DesignatorAssistantCG extends AssistantBase
{
	public DesignatorAssistantCG(AssistantManager assistantManager)
	{
		super(assistantManager);
	}

	public AIdentifierStateDesignatorCG consMember(PTypeCG type, String name)
	{
		AIdentifierStateDesignatorCG member = new AIdentifierStateDesignatorCG();
		member.setType(type);
		member.setName(name);
		
		return member;
	}
}
