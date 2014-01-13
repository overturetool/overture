package org.overture.typechecker.utilities;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.AnswerAdaptor;
import org.overture.ast.definitions.AImportedDefinition;
import org.overture.ast.definitions.AInheritedDefinition;
import org.overture.ast.definitions.ARenamedDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.node.INode;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

/**
 * This class implements a way to dereference a node in the AST
 * 
 * @author kel
 */

public class Dereferer extends AnswerAdaptor<PDefinition>
{

	protected ITypeCheckerAssistantFactory af;

	public Dereferer(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	@Override
	public PDefinition caseAImportedDefinition(AImportedDefinition node)
			throws AnalysisException
	{
		return node.getDef().apply(THIS);
	}

	@Override
	public PDefinition caseAInheritedDefinition(AInheritedDefinition node)
			throws AnalysisException
	{
		return node.getSuperdef().apply(THIS);
	}

	@Override
	public PDefinition caseARenamedDefinition(ARenamedDefinition node)
			throws AnalysisException
	{
		return node.getDef().apply(THIS);
	}

	@Override
	public PDefinition defaultPDefinition(PDefinition node)
			throws AnalysisException
	{
		return node;
	}

	@Override
	public PDefinition createNewReturnValue(INode node)
	{
		assert false : "should not happen";
		return null;
	}

	@Override
	public PDefinition createNewReturnValue(Object node)
	{
		assert false : "should not happen";
		return null;
	}

}
