package org.overture.typechecker.utilities;

import org.overture.ast.analysis.AnalysisAdaptor;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.AEqualsDefinition;
import org.overture.ast.definitions.AMultiBindListDefinition;
import org.overture.ast.definitions.AStateDefinition;
import org.overture.ast.definitions.AValueDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;
import org.overture.typechecker.assistant.definition.AEqualsDefinitionAssistantTC;
import org.overture.typechecker.assistant.definition.AMultiBindListDefinitionAssistantTC;
import org.overture.typechecker.assistant.definition.AStateDefinitionAssistantTC;
import org.overture.typechecker.assistant.definition.AValueDefinitionAssistantTC;

public class UnusedChecker extends AnalysisAdaptor
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected ITypeCheckerAssistantFactory af;

	public UnusedChecker(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}
	@Override
	public void caseAEqualsDefinition(AEqualsDefinition node)
			throws AnalysisException
	{
		AEqualsDefinitionAssistantTC.unusedCheck(node);
	}
	
	@Override
	public void caseAMultiBindListDefinition(AMultiBindListDefinition node)
			throws AnalysisException
	{
		AMultiBindListDefinitionAssistantTC.unusedCheck(node);
	}
	
	@Override
	public void caseAStateDefinition(AStateDefinition node)
			throws AnalysisException
	{
		AStateDefinitionAssistantTC.unusedCheck(node);
	}
	
	@Override
	public void caseAValueDefinition(AValueDefinition node)
			throws AnalysisException
	{
		AValueDefinitionAssistantTC.unusedCheck(node);
	}
	@Override
	public void defaultPDefinition(PDefinition node) throws AnalysisException
	{
		af.createPDefinitionAssistant().unusedCheckBaseCase(node);
	}
	
}
