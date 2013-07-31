package org.overture.pog.utility;

import org.overture.pog.pub.IPogAssistantFactory;
import org.overture.typechecker.assistant.TypeCheckerAssistantFactory;

public class PogAssistantFactory extends TypeCheckerAssistantFactory implements IPogAssistantFactory
{
	@Override
	public PDefinitionAssistantPOG createPDefinitionAssistant()
	{
		return new PDefinitionAssistantPOG(this);
	}
	
	@Override
	public ACaseAlternativeAssistantPOG createACaseAlternativeAssistant()
	{
		return new ACaseAlternativeAssistantPOG(this);
	}
}
