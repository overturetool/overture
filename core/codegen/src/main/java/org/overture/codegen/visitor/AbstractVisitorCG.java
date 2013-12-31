package org.overture.codegen.visitor;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.expressions.AUndefinedExp;
import org.overture.ast.node.INode;
import org.overture.codegen.logging.Logger;

public class AbstractVisitorCG<Q extends OoAstInfo, A extends org.overture.codegen.cgast.INode> extends QuestionAnswerAdaptor<Q, A>
{

	@Override
	public A defaultINode(INode node,
			Q question) throws AnalysisException
	{
		
		if(node instanceof AUndefinedExp)
		{
			Logger.getLog().printErrorln("Ignoring undefined expression in " + this.getClass().getName());
			return null;
		}
		
		question.addUnsupportedNode(node);
		//Logger.getLog().printErrorln("Code generation does not support the following VDM construct: " + node.getClass().getName() + ": " + node.toString());
		
		return null;
	}

	@Override
	public A createNewReturnValue(INode node, Q question)
			throws AnalysisException
	{
		return null;
	}

	@Override
	public A createNewReturnValue(Object node, Q question)
			throws AnalysisException
	{
		return null;
	}
	
}
