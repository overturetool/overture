package org.overture.typechecker.visitor;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.node.INode;
import org.overture.ast.types.PType;
import org.overture.typechecker.TypeCheckInfo;

public class AbstractTypeCheckVisitor extends
		QuestionAnswerAdaptor<TypeCheckInfo, PType>
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	final protected QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor;

	public AbstractTypeCheckVisitor(
			QuestionAnswerAdaptor<TypeCheckInfo, PType> visitor,
			QuestionAnswerAdaptor<TypeCheckInfo, PType> typeCheckVisitor)
	{
		super(visitor);
		this.rootVisitor = typeCheckVisitor;
	}

	public AbstractTypeCheckVisitor(
			)
	{
		super();
		this.rootVisitor = null;
	}

	@Override
	public PType createNewReturnValue(INode node, TypeCheckInfo question)
	{
		return null;
	}

	@Override
	public PType createNewReturnValue(Object node, TypeCheckInfo question)
	{
		return null;
	}
	
	@Override
	public PType defaultINode(INode node, TypeCheckInfo question)
			throws AnalysisException
	{
		return rootVisitor.defaultINode(node,question);
	}

}
