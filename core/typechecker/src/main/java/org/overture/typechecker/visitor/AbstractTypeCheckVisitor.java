package org.overture.typechecker.visitor;

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

	public AbstractTypeCheckVisitor(
			QuestionAnswerAdaptor<TypeCheckInfo, PType> visitor)
	{
		super(visitor);
	}

	public AbstractTypeCheckVisitor()
	{
		super();
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

}
