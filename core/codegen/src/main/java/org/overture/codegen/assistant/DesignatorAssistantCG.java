package org.overture.codegen.assistant;

import org.overture.codegen.cgast.statements.AIdentifierStateDesignatorCG;

public class DesignatorAssistantCG
{
	public static AIdentifierStateDesignatorCG consMember(String name)
	{
		AIdentifierStateDesignatorCG member = new AIdentifierStateDesignatorCG();
		member.setName(name);
		
		return member;
	}
}
