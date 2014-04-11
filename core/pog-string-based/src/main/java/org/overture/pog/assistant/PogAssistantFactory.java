package org.overture.pog.assistant;

import org.overture.typechecker.assistant.TypeCheckerAssistantFactory;

public class PogAssistantFactory extends TypeCheckerAssistantFactory implements
		IPogAssistantFactory
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
