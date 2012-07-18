
package org.overture.ast.analysis;


import org.overture.ast.analysis.intf.IAnalysisInterpreter;
import org.overture.ast.analysis.intf.IQuestionAnswerInterpreter;
import org.overture.ast.expressions.AE2ExpInterpreter;
import org.overture.ast.expressions.AE3ExpInterpreter;
import org.overture.ast.expressions.AE4ExpInterpreter;
import org.overture.ast.expressions.PExpInterpreter;
import org.overture.ast.node.INodeInterpreter;
import org.overture.ast.node.ITokenInterpreter;
import org.overture.ast.node.tokens.TIntInterpreter;
import org.overture.ast.statements.AS1StmInterpreter;


/**
* Generated file by AST Creator
* @author Kenneth Lausdahl
*
*/
public class QuestionAnswerAdaptorInterpreter<Q, A> extends QuestionAnswerAdaptor<Q, A> implements IQuestionAnswerInterpreter<Q, A>
{
	private static final long serialVersionUID = 1L;



	/**
	 * Creates a new {@link QuestionAnswerAdaptorInterpreter} node with no children.
	 */
	public QuestionAnswerAdaptorInterpreter()
	{

	}


	/**
	* Called by the {@link ITokenInterpreter} node from {@link ITokenInterpreter#apply(IAnalysisInterpreter)}.
	* @param node the calling {@link ITokenInterpreter} node
	*/
	public A caseTIntInterpreter(TIntInterpreter node, Q question)
	{
		return defaultITokenInterpreter(node, question);
	}


	/**
	* Called by the {@link PExpInterpreter} node from {@link PExpInterpreter#apply(IAnalysisInterpreter)}.
	* @param node the calling {@link PExpInterpreter} node
	*/
	public A defaultPExpInterpreter(PExpInterpreter node, Q question)
	{
		return defaultPExp(node, question);
	}




	/**
	* Called by the {@link AE2ExpInterpreter} node from {@link AE2ExpInterpreter#apply(IAnalysisInterpreter)}.
	* @param node the calling {@link AE2ExpInterpreter} node
	*/
	public A caseAE2ExpInterpreter(AE2ExpInterpreter node, Q question)
	{
		return defaultPExpInterpreter(node, question);
	}


	/**
	* Called by the {@link INodeInterpreter} node from {@link INodeInterpreter#apply(IAnalysisInterpreter)}.
	* @param node the calling {@link INodeInterpreter} node
	*/
	public A defaultINodeInterpreter(INodeInterpreter node, Q question)
	{
		return defaultINode(node, question);
	}


	/**
	* Called by the {@link ITokenInterpreter} node from {@link ITokenInterpreter#apply(IAnalysisInterpreter)}.
	* @param node the calling {@link ITokenInterpreter} node
	*/
	public A defaultITokenInterpreter(ITokenInterpreter node, Q question)
	{
		return defaultIToken(node, question);
	}


	@Override
	public A caseAE3ExpInterpreter(AE3ExpInterpreter node, Q question)
	{
		return defaultPExpInterpreter(node, question);
	}
	
	@Override
	public A caseAE4ExpInterpreter(AE4ExpInterpreter node, Q question)
	{
		return defaultPExpInterpreter(node, question);
	}


	@Override
	public A caseAS1StmInterpreter(AS1StmInterpreter node, Q question)
	{
		return defaultPStmInterpreter(node, question);
	}


	private A defaultPStmInterpreter(AS1StmInterpreter node, Q question)
	{
		return defaultINodeInterpreter(node, question);
	}



}
