package org.overture.pog.utility;

import org.overture.pog.pub.IPogAssistantFactory;
import org.overture.pog.visitors.IInvExpGetVisitor;
import org.overture.pog.visitors.ILocaleExtractVisitor;
import org.overture.pog.visitors.IVariableSubVisitor;
import org.overture.pog.visitors.StateDesignatorNameGetter;
import org.overture.pog.visitors.VariableSubVisitor;
import org.overture.pog.visitors.VdmInvExpGetVisitor;
import org.overture.pog.visitors.VdmLocaleExtractor;
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

}
