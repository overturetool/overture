package org.overture.pog.pub;

import org.overture.pog.utility.ACaseAlternativeAssistantPOG;
import org.overture.pog.utility.PDefinitionAssistantPOG;
import org.overture.pog.visitors.StateDesignatorNameGetter;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public interface IPogAssistantFactory extends ITypeCheckerAssistantFactory
{
		ACaseAlternativeAssistantPOG createACaseAlternativeAssistant();
		PDefinitionAssistantPOG createPDefinitionAssistant();
		
		
		StateDesignatorNameGetter createStateDesignatorNameGetter();
		
}
