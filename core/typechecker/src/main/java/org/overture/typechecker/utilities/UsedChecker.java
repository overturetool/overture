package org.overture.typechecker.utilities;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.AnswerAdaptor;
import org.overture.ast.definitions.AExternalDefinition;
import org.overture.ast.definitions.AInheritedDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;
import org.overture.typechecker.assistant.definition.AExternalDefinitionAssistantTC;
import org.overture.typechecker.assistant.definition.AInheritedDefinitionAssistantTC;

public class UsedChecker extends AnswerAdaptor<Boolean>
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected ITypeCheckerAssistantFactory af;

	public UsedChecker(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}
	
	@Override
	public Boolean caseAExternalDefinition(AExternalDefinition node)
			throws AnalysisException
	{
		return AExternalDefinitionAssistantTC.isUsed(node);
	}
	
	@Override
	public Boolean caseAInheritedDefinition(AInheritedDefinition node)
			throws AnalysisException
	{
		return AInheritedDefinitionAssistantTC.isUsed(node);
	}
	
	@Override
	public Boolean defaultPDefinition(PDefinition node)
			throws AnalysisException
	{
		return node.getUsed();
	}
	
//	if (d instanceof AExternalDefinition)
//	{
//		return AExternalDefinitionAssistantTC.isUsed((AExternalDefinition) d);
//	} else if (d instanceof AInheritedDefinition)
//	{
//		return AInheritedDefinitionAssistantTC.isUsed((AInheritedDefinition) d);
//	} else
//	{
//		return d.getUsed();
//	}

}
