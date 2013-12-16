package org.overture.typechecker.utilities;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.AnswerAdaptor;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.node.INode;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;
import org.overture.typechecker.assistant.definition.SClassDefinitionAssistantTC;

/**
 * This class implements a way to find the self definition from a node in the AST
 * 
 * @author kel
 */
public class SelfDefinitionFinder extends AnswerAdaptor<PDefinition>
{
	/**
	 * Generated serial version
	 */
	private static final long serialVersionUID = 1L;

	protected ITypeCheckerAssistantFactory af;

	public SelfDefinitionFinder(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	@Override
	public PDefinition defaultSClassDefinition(SClassDefinition node)
			throws AnalysisException
	{
		return af.createSClassDefinitionAssistant().getSelfDefinition((SClassDefinition) node);
	}

	@Override
	public PDefinition defaultPDefinition(PDefinition node)
			throws AnalysisException
	{
		return node.getClassDefinition().apply(THIS);
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
