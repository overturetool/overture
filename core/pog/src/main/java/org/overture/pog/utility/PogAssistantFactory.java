package org.overture.pog.utility;

import org.overture.pog.pub.IPogAssistantFactory;
import org.overture.typechecker.assistant.TypeCheckerAssistantFactory;
//TODO Add assistant Javadoc
/** 
 * THe assistant factory for the pog. It provides all the functionality of 
 * the overture typechecker (maybe that is what we parameterize?)
 * 
 * PLus any new pog bits!
 * @author ldc
 *
 */
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
