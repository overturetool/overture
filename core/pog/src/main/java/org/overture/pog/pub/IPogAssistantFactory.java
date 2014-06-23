package org.overture.pog.pub;

import org.overture.pog.utility.ACaseAlternativeAssistantPOG;
import org.overture.pog.utility.PDefinitionAssistantPOG;
import org.overture.pog.visitors.ILocaleExtractVisitor;
import org.overture.pog.visitors.IVariableSubVisitor;
import org.overture.pog.visitors.StateDesignatorNameGetter;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

/**
 * A factory for creating assistants at the POG level.
 */
public interface IPogAssistantFactory extends ITypeCheckerAssistantFactory
{
		
		/* (non-Javadoc)
		 * @see org.overture.typechecker.assistant.ITypeCheckerAssistantFactory#createACaseAlternativeAssistant()
		 */
		ACaseAlternativeAssistantPOG createACaseAlternativeAssistant();
		
		/* (non-Javadoc)
		 * @see org.overture.typechecker.assistant.ITypeCheckerAssistantFactory#createPDefinitionAssistant()
		 */
		PDefinitionAssistantPOG createPDefinitionAssistant();
		
		StateDesignatorNameGetter getStateDesignatorNameGetter();
		IVariableSubVisitor getVarSubVisitor();
		ILocaleExtractVisitor getLocaleExtractVisitor();
		
}
