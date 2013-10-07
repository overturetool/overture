package org.overture.typechecker.utilities;

import org.overture.ast.analysis.AnalysisAdaptor;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.AEqualsDefinition;
import org.overture.ast.definitions.AMultiBindListDefinition;
import org.overture.ast.definitions.AStateDefinition;
import org.overture.ast.definitions.AValueDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;
import org.overture.typechecker.assistant.definition.PDefinitionListAssistantTC;

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
		if (node.getDefs() != null)
		{
			PDefinitionListAssistantTC.unusedCheck(node.getDefs());
		}
	}
	
	@Override
	public void caseAMultiBindListDefinition(AMultiBindListDefinition node)
			throws AnalysisException
	{
		if (node.getDefs() != null)
		{
			PDefinitionListAssistantTC.unusedCheck(node.getDefs());
		}
	}
	
	@Override
	public void caseAStateDefinition(AStateDefinition node)
			throws AnalysisException
	{
		PDefinitionListAssistantTC.unusedCheck(node.getStateDefs());
	}
	
	@Override
	public void caseAValueDefinition(AValueDefinition node)
			throws AnalysisException
	{
		if (node.getUsed()) // Indicates all definitions exported (used)
		{
			return;
		}

		if (node.getDefs() != null)
		{
			for (PDefinition def : node.getDefs())
			{
				//PDefinitionAssistantTC.unusedCheck(def);
				def.apply(THIS);
			}
		}

	}
	@Override
	public void defaultPDefinition(PDefinition node) throws AnalysisException
	{
		af.createPDefinitionAssistant().unusedCheckBaseCase(node);
	}
	
}
