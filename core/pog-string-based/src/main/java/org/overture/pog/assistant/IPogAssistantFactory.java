package org.overture.pog.assistant;

import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public interface IPogAssistantFactory extends ITypeCheckerAssistantFactory
{
		ACaseAlternativeAssistantPOG createACaseAlternativeAssistant();
		PDefinitionAssistantPOG createPDefinitionAssistant();
}
