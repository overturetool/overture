package org.overture.pog.pub;

import org.overture.pog.utility.PDefinitionAssistantPOG;
import org.overture.pog.utility.UniqueNameGenerator;
import org.overture.pog.visitors.*;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

/**
 * A factory for creating assistants at the POG level.
 */
public interface IPogAssistantFactory extends ITypeCheckerAssistantFactory {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.overture.typechecker.assistant.ITypeCheckerAssistantFactory#
	 * createPDefinitionAssistant()
	 */
	PDefinitionAssistantPOG createPDefinitionAssistant();

	StateDesignatorNameGetter getStateDesignatorNameGetter();

	IVariableSubVisitor getVarSubVisitor();

	ILocaleExtractVisitor getLocaleExtractVisitor();

	IInvExpGetVisitor getInvExpGetVisitor();

	PatternToExpVisitor getPatternToExpVisitor(
			UniqueNameGenerator uniqueNameGen);

}
