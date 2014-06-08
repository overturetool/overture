package org.overture.codegen.visitor;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.codegen.cgast.PCG;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.ir.SourceNode;

public class CGVisitor<A extends PCG> extends QuestionAnswerAdaptor<IRInfo, A>
{
	private AbstractVisitorCG<IRInfo, A> irBuilder;
	
	public CGVisitor(AbstractVisitorCG<IRInfo, A> visitor)
	{
		this.irBuilder = visitor;
	}

	@Override
	public A defaultINode(org.overture.ast.node.INode node,
			IRInfo question) throws AnalysisException
	{
		A irNode = node.apply(irBuilder, question);
		
		if(irNode != null)
		{
			irNode.setSourceNode(new SourceNode(node));
		}

		return irNode;
	}
	
	@Override
	public A createNewReturnValue(org.overture.ast.node.INode node,
			IRInfo question) throws AnalysisException
	{
		return null;
	}

	@Override
	public A createNewReturnValue(Object node,
			IRInfo question) throws AnalysisException
	{
		return null;
	}
}
