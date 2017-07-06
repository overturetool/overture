package org.overture.typechecker.utilities;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.AnswerAdaptor;
import org.overture.ast.definitions.AClassClassDefinition;
import org.overture.ast.definitions.AImportedDefinition;
import org.overture.ast.definitions.AInheritedDefinition;
import org.overture.ast.definitions.ARenamedDefinition;
import org.overture.ast.definitions.ATypeDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.node.INode;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

/**
 * This class implements a way to check if a node is a type definition.
 */
public class TypeDefinitionChecker extends AnswerAdaptor<Boolean>
{
	protected ITypeCheckerAssistantFactory af;

	public TypeDefinitionChecker(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	@Override
	public Boolean caseATypeDefinition(ATypeDefinition node) throws AnalysisException
	{
		return true;
	};
	
	@Override
	public Boolean caseAClassClassDefinition(AClassClassDefinition node) throws AnalysisException
	{
		return true;	// Class is regarded as a type
	}
	
	@Override
	public Boolean caseAImportedDefinition(AImportedDefinition node) throws AnalysisException
	{
		return node.getDef().apply(this);
	}
	
	public Boolean caseAInheritedDefinition(AInheritedDefinition node) throws AnalysisException
	{
		return node.getSuperdef().apply(this);
	};

	@Override
	public Boolean caseARenamedDefinition(ARenamedDefinition node)
		throws AnalysisException
	{
		return node.getDef().apply(this);
	}
	
	@Override
	public Boolean defaultPDefinition(PDefinition node)
			throws AnalysisException
	{
		return false;
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
}
