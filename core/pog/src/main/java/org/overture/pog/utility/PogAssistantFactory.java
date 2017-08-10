package org.overture.pog.utility;

import org.overture.pog.pub.IPogAssistantFactory;
import org.overture.pog.visitors.*;
import org.overture.typechecker.assistant.TypeCheckerAssistantFactory;

/**
 * THe assistant factory for the pog. It provides all the functionality of the overture typechecker (maybe that is what
 * we parameterize?) PLus any new pog bits!
 * 
 * @author ldc
 */
public class PogAssistantFactory extends TypeCheckerAssistantFactory implements
		IPogAssistantFactory
{
	@Override
	public PDefinitionAssistantPOG createPDefinitionAssistant()
	{
		return new PDefinitionAssistantPOG(this);
	}

	@Override
	public StateDesignatorNameGetter getStateDesignatorNameGetter()
	{
		return new StateDesignatorNameGetter();
	}

	@Override
	public IVariableSubVisitor getVarSubVisitor()
	{
		return new VariableSubVisitor();
	}

	@Override
	public ILocaleExtractVisitor getLocaleExtractVisitor()
	{
		return new VdmLocaleExtractor();
	}

	@Override
	public IInvExpGetVisitor getInvExpGetVisitor()
	{
		return new VdmInvExpGetVisitor();
	}

	@Override public PatternToExpVisitor getPatternToExpVisitor(
			UniqueNameGenerator uniqueNameGen)
	{
		return new PatternToExpVisitor(uniqueNameGen,this);
	}

}
