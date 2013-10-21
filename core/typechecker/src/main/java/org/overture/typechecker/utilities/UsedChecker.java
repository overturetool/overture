package org.overture.typechecker.utilities;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.AnswerAdaptor;
import org.overture.ast.definitions.AExternalDefinition;
import org.overture.ast.definitions.AInheritedDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.node.INode;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;
import org.overture.typechecker.assistant.definition.AExternalDefinitionAssistantTC;
import org.overture.typechecker.assistant.definition.AInheritedDefinitionAssistantTC;

/**
 * This class implements a way to check if a node is used in the AST
 * 
 * @author kel
 */
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

	@Override
	public Boolean createNewReturnValue(INode node)
	{
		assert false : "should not happen";
		return null;
	}

	@Override
	public Boolean createNewReturnValue(Object node)
	{
		assert false : "should not happen";
		return null;
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
