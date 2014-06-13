package org.overture.codegen.assistant;

import org.overture.codegen.cgast.STypeCG;
import org.overture.codegen.cgast.statements.AIdentifierStateDesignatorCG;

public class DesignatorAssistantCG extends AssistantBase
{
	public DesignatorAssistantCG(AssistantManager assistantManager)
	{
		super(assistantManager);
	}

	public AIdentifierStateDesignatorCG consMember(STypeCG type, String name)
	{
		AIdentifierStateDesignatorCG member = new AIdentifierStateDesignatorCG();
		member.setType(type);
		member.setName(name);
		
		return member;
	}
}
