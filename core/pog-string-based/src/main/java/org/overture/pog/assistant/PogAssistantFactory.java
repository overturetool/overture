package org.overture.pog.assistant;

import org.overture.typechecker.assistant.TypeCheckerAssistantFactory;

public class PogAssistantFactory extends TypeCheckerAssistantFactory implements
		IPogAssistantFactory
{
	static
	{
		// FIXME: remove this when conversion to factory obtained assistants are completed.
		// init(new AstAssistantFactory());
		init(new PogAssistantFactory());
	}

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
