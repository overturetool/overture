package org.overture.typechecker.visitor;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.analysis.intf.IQuestionAnswer;
import org.overture.ast.node.INode;
import org.overture.ast.types.PType;
import org.overture.typechecker.TypeCheckInfo;

public class AbstractTypeCheckVisitor extends
		QuestionAnswerAdaptor<TypeCheckInfo, PType>
{

	public AbstractTypeCheckVisitor(
			IQuestionAnswer<TypeCheckInfo, PType> visitor)
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

	@Override
	public PType defaultINode(INode node, TypeCheckInfo question)
			throws AnalysisException
	{
		return THIS.defaultINode(node, question);
	}

}
